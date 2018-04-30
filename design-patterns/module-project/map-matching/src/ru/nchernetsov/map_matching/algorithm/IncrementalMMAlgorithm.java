package ru.nchernetsov.map_matching.algorithm;

import ru.nchernetsov.geometry.Pos3D;
import ru.nchernetsov.geometry.PosGPS;
import ru.nchernetsov.map_matching.EdgeInfo;
import ru.nchernetsov.map_matching.MMResult;
import ru.nchernetsov.map_matching.ProjOnEdge;
import ru.nchernetsov.map_matching.RouteEdge;
import ru.nchernetsov.map_matching.cache.CacheFilesHandler;
import ru.nchernetsov.map_matching.interfaces.ActionOnShift;
import ru.nchernetsov.map_matching.interfaces.ActionOnTransition;
import ru.nchernetsov.map_matching.interfaces.DevicesMMResultsHolder;
import ru.nchernetsov.map_matching.interfaces.MapMatchingAlgorithm;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Алгоритм Map Matching'а основан на подходах, изложенных в статье "Инкрементальный алгоритм привязки GPS-трека к дорожному графу"
// https://habrahabr.ru/company/mailru/blog/157883/ (папка /docs)
// Общая блок-схема алгоритма из статьи (наш алгоритм несколько отличается):
// 1. Выбрать все рёбра графа с геометрией, пересекающей дельта-окрестность первой точки трека
// 2. Оценить все выбранные рёбра с помощью формулы метрики (оценочной формулы для инкрементального алгоритма привязки данных)
// 3. Выбрать ребро с наибольшей оценкой. Сделать его текущим и добавить его к готовому маршруту
// 4. Если ближайшая к точке трека точка на геометрии ребра находится не на конце ребра, то выбрать следующую точку трека (если больше точек нет, то привязка закончена)
// 5. Выбрать все исходящие из текущего рёбра графа и текущее ребро
// 6. Перейти к пункту 2
public class IncrementalMMAlgorithm extends MMAlgorithmCommon implements MapMatchingAlgorithm {
    private final AtomicInteger recursiveCallCounter = new AtomicInteger();  // счётчик количества рекурсивных вызовов для ограничения глубины рекурсии

    public IncrementalMMAlgorithm(CacheFilesHandler cacheFilesHandler, Map<String, Object> algorithmConfig) {
        super(cacheFilesHandler, algorithmConfig);
    }

    @Override
    public Optional<MMResult> applyMM(long deviceID, PosGPS gps, DevicesMMResultsHolder deviceMMResultsHolder, ActionOnTransition onTransition, ActionOnShift onShift) {
        recursiveCallCounter.set(0);
        return commonPartOfMapMatchingAlgorithm(deviceID, gps, deviceMMResultsHolder, onTransition, onShift);
    }

    @Override
    List<EdgeInfo> getPassedByContinuityEdgesList(MMResult mmResult, MMResult prevMMResult) {
        ProjOnEdge prevProj = prevMMResult.getProjOnEdge();
        ProjOnEdge lastProj = mmResult.getProjOnEdge();

        List<EdgeInfo> passedByContinuityEdgesList = new ArrayList<>();
        double traveledPathLength = 0.0;
        double lastRelPosOnEdge = lastProj.getRelPosOnEdge();
        while (lastProj.getRouteEdgeID() != prevProj.getRouteEdgeID()) {
            traveledPathLength += lastProj.getPassedLength();
            double edgeLength = lastProj.edge.getLength();
            passedByContinuityEdgesList.add(
                new EdgeInfo(
                    lastProj.getRouteEdgeOsmId(),
                    lastProj.getRouteEdgeOsmVersion(),
                    lastProj.getRouteEdgeID(),
                    edgeLength,
                    traveledPathLength  // это вспомогательное значение, которое будет использовано ниже
                )
            );
            lastProj = lastProj.parentProj;
        }
        traveledPathLength += prevProj.getRestLength();

        long prevTimestamp = prevProj.pos.getTimestamp();
        long currTimestamp = lastProj.pos.getTimestamp();

        for (EdgeInfo edgeInfo : passedByContinuityEdgesList) {
            double k0 = (traveledPathLength - edgeInfo.getRelPosOnEdge()) / traveledPathLength;
            edgeInfo.setTimestamp((long) ((1 - k0) * prevTimestamp + k0 * currTimestamp));  // время прихода точки в начало ребра
            edgeInfo.setRelPosOnEdge(1.0);
        }

        passedByContinuityEdgesList.get(0).setRelPosOnEdge(lastRelPosOnEdge);  // для последнего ребра относительную позицию знаем

        Collections.reverse(passedByContinuityEdgesList);  // переворачиваем список, чтобы рёбра шли в нём по ходу движения (1,2,...,N)

        return passedByContinuityEdgesList;
    }

    @Override
    Optional<MMResult> projOnEdgeByContinuity(MMResult prevMMResult, PosGPS gps, Pos3D pos) {
        ProjOnEdge prevProj = prevMMResult.projOnEdge;
        Optional<ProjOnEdge> nextProj = findNextProjOnEdge(prevProj, pos);
        return nextProj.map(proj -> new MMResult(proj, 0.0));
    }

    private Optional<ProjOnEdge> findNextProjOnEdge(ProjOnEdge prevProjOnEdge, Pos3D newPos) {
        Pos3D prevPos = prevProjOnEdge.pos;
        double deltaTau = deltaTauSec(newPos, prevPos);
        double searchInOutEdgesPathLimit = searchByContinuityPathLimitFactor * (prevPos.getSpeed() * deltaTau);
        ProjOnEdge newProjOnPrevEdge = new ProjOnEdge(
            prevProjOnEdge.edge, prevProjOnEdge.getRouteEdgeID(), newPos, this::metric);  // проекция новой точки на предыдущее ребро

        if (newProjOnPrevEdge.isProjInsideEdge()) {
            if (isSlowMotionAgainstDirection(prevProjOnEdge, newProjOnPrevEdge)) {
                // при скорости меньше 1 км/ч полагаем, что движение назад - это ошибка, потому привязку к ребру не меняем
                // также предполагается, что по односторонней дороге можно двигаться только вперёд
                return Optional.of(prevProjOnEdge);
            }
            calcMetricDegradation(prevProjOnEdge, newProjOnPrevEdge);
            //Метрика 3 раза ухудшилась => проекция не найдена
            if (newProjOnPrevEdge.metricDegradation == 0) {
                return Optional.empty();
            }

            //При отрицательной метрике пытается рекурсивно пройти по внешним рёбрам и найти там подходящее
            if (newProjOnPrevEdge.getBestSegmentMetric() < 0.0) {
                Optional<ProjOnEdge> bestProjInOutEdges = findProjInOutEdges(prevProjOnEdge, newProjOnPrevEdge, newPos, searchInOutEdgesPathLimit);
                if (bestProjInOutEdges.isPresent()) {
                    if (bestProjInOutEdges.get().isProjMetricBetterThan(newProjOnPrevEdge)) {
                        return bestProjInOutEdges;
                    }
                }
            }
            // Вычисленная проекция на то же ребро
            return Optional.of(newProjOnPrevEdge);
        } else {
            return findProjInOutEdges(prevProjOnEdge, newProjOnPrevEdge, newPos, searchInOutEdgesPathLimit);
        }
    }

    private Optional<ProjOnEdge> findProjInOutEdges(ProjOnEdge prevProj, ProjOnEdge newProj, Pos3D pos, double limitOfOutEdgesSearch) {
        newProj.pathTraveledByContinuity = prevProj.calcPathFromProjToEndEdge();
        Map<Integer, Double> visitedEdges = new HashMap<>();
        visitedEdges.put(newProj.getRouteEdgeID(), newProj.pathTraveledByContinuity);  // Map вида "ребро - путь по непрерывности"
        Optional<ProjOnEdge> bestProjInOutEdges = findBestProjInOutEdges(newProj, pos, prevProj.outEdges(), visitedEdges, limitOfOutEdgesSearch);
        visitedEdges.clear();
        return bestProjInOutEdges;
    }

    private Optional<ProjOnEdge> findBestProjInOutEdges(ProjOnEdge newProj, Pos3D newPos, List<Integer> outEdges, Map<Integer, Double> visitedEdges, double searchPathLimit) {
        int recursiveCallCount = recursiveCallCounter.incrementAndGet();

        //Путь, пройденный по непрерывности, превышает диапазон поиска
        if (newProj.pathTraveledByContinuity > searchPathLimit)
            return Optional.empty();
        // Количество рекурсивных вызовов превышаем максимально допустимое
        if (recursiveCallCount > maxRecursiveCallCount)
            return Optional.empty();
        // обходим исходящие рёбра
        if (!outEdges.isEmpty()) {
            List<ProjOnEdge> projsOnOutEdges = new ArrayList<>();
            // Проекция на исходящие рёбра
            for (Integer outEdgeID : outEdges) {
                RouteEdge outEdge = createEdgeByEdgeID(outEdgeID);
                if (visitedEdges.containsKey(outEdgeID) &&
                    newProj.pathTraveledByContinuity + outEdge.getLength() > visitedEdges.get(outEdgeID)) {
                    continue;
                }
                ProjOnEdge projOnOutEdge = new ProjOnEdge(outEdge, outEdgeID, newPos, this::metric);
                projOnOutEdge.parentProj = newProj;
                projOnOutEdge.pathTraveledByContinuity = newProj.pathTraveledByContinuity + outEdge.getLength();
                projsOnOutEdges.add(projOnOutEdge);
                visitedEdges.put(outEdgeID, projOnOutEdge.pathTraveledByContinuity);
            }

            // Сортируем проекции в исходящих рёбрах по убыванию метрики, чтобы начинать поиск с проекций с наилучшей метрикой
            projsOnOutEdges.sort(ProjOnEdge::compare);

            // Список "хороших" проекций
            List<ProjOnEdge> subProjs = new ArrayList<>();

            for (ProjOnEdge projOnOutEdge : projsOnOutEdges) {
                // if (projOnOutEdge.isProjInsideEdge() && projOnOutEdge.hasPositiveMetric()) {
                if (projOnOutEdge.hasPositiveMetric()) {  // учитываем все точки с положительной метрикой
                    subProjs.add(projOnOutEdge);
                } else {
                    Optional<ProjOnEdge> bestProjInOutEdges = findBestProjInOutEdges(projOnOutEdge, newPos, projOnOutEdge.outEdges(), visitedEdges, searchPathLimit);
                    bestProjInOutEdges.ifPresent(subProjs::add);
                }
            }

            projsOnOutEdges.clear();

            List<ProjOnEdge> subProjCandidate = subProjs.stream()
                .filter(proj -> (proj != null && proj.hasPositiveMetric()))  // учитываем все точки с положительной метрикой
                //.filter(proj -> (proj != null && proj.isProjInsideEdge() && proj.hasPositiveMetric()))
                .collect(Collectors.toList());
            subProjs.clear();

            if (!subProjCandidate.isEmpty()) {
                subProjCandidate.sort(ProjOnEdge::compare);  // сортируем по возрастанию метрики, а если метрики одинаковые - то по возрастанию длины пути "по непрерывности"
                ProjOnEdge foundBestProjInOutEdges = subProjCandidate.get(0);
                subProjCandidate.clear();
                return Optional.of(foundBestProjInOutEdges);
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private void calcMetricDegradation(ProjOnEdge prevProjOnEdge, ProjOnEdge newProjOnPrevEdge) {
        newProjOnPrevEdge.metricDegradation = prevProjOnEdge.metricDegradation;
        if (!newProjOnPrevEdge.isProjMetricBetterThan(prevProjOnEdge)) {
            newProjOnPrevEdge.metricDegradation--;
        } else {
            newProjOnPrevEdge.metricDegradation = maxMetricDegradationCount;
        }
    }
}
