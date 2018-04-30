package ru.nchernetsov.map_matching.algorithm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.nchernetsov.geometry.FromGeoTo3DTransformer;
import ru.nchernetsov.geometry.Point3D;
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
import java.util.stream.Collectors;

import static ru.nchernetsov.geometry.GeometryOperation.distanceBetweenTwo3Dpoints;

/**
 * Алгоритм Map Matching'а с поиском пути по исходящим рёбрам от предыдущей точки до рассматриваемой
 */
public class MMAlgorithmFindPath extends MMAlgorithmCommon implements MapMatchingAlgorithm {
    private final double distanceCanBePositiveMetric;
    private int goalEdgeId = 0;  // id "целевого" ребра в алгоритме A*
    // очередь с приоритетом для хранения путей (множество частных решений) при работе алгоритма A*
    // для каждого пути также сохраняется длина
    private final Queue<Pair<List<Integer>, Double>> partitialSolutionsSet;
    private final Set<Integer> closedEdges;  // Множество уже пройденных рёбер

    private final List<Integer> passedByContinuityEdgesList = new ArrayList<>();  // список рёбер, пройденных "по непрерывности"

    public MMAlgorithmFindPath(CacheFilesHandler cacheFilesHandler, Map<String, Object> algorithmConfig) {
        super(cacheFilesHandler, algorithmConfig);
        distanceCanBePositiveMetric = findDistanceCanBePositiveMetric();
        partitialSolutionsSet = new PriorityQueue<>((pathLength1, pathLength2) -> {
            double f1 = heuristicCostFunction(pathLength1, goalEdgeId);
            double f2 = heuristicCostFunction(pathLength2, goalEdgeId);
            return Double.compare(f1, f2);
        });
        closedEdges = new HashSet<>();
    }

    // Основной метод алгоритма
    @Override
    public Optional<MMResult> applyMM(long deviceId, PosGPS gps, DevicesMMResultsHolder deviceMMResultsHolder, ActionOnTransition onTransition, ActionOnShift onShift) {
        passedByContinuityEdgesList.clear();
        return commonPartOfMapMatchingAlgorithm(deviceId, gps, deviceMMResultsHolder, onTransition, onShift);
    }

    @Override
    Optional<MMResult> projOnEdgeByContinuity(MMResult prevMMResult, PosGPS gps, Pos3D pos) {
        // поиск в R*-дереве вероятных кандидатов на следующую проекцию
        FromGeoTo3DTransformer transformer = new FromGeoTo3DTransformer(gps.getLat(), gps.getLon());
        double[] halfSizeBoxInDegrees = transformer.makeBoxHalfSizeInDegrees(distanceCanBePositiveMetric);
        List<Integer> possibleCandidateGeoms = findGeomsInRTree(gps, halfSizeBoxInDegrees);

        // список проекций на рёбра-кандиданы с положительной метрикой, отсортированный по убыванию метрики
        List<ProjOnEdge> projsOnCandidateGeoms = selectProjsWithPositiveMetric(possibleCandidateGeoms, pos);
        if (!projsOnCandidateGeoms.isEmpty()) {
            ProjOnEdge prevProj = prevMMResult.projOnEdge;
            // Находим среднюю скорость по двум точкам
            double prevSpeed = prevProj.pos.getSpeed();
            double currSpeed = pos.getSpeed();
            double averageSpeed = (prevSpeed + currSpeed)/2.0;
            // Ограничим длину поиска в исходящих рёбрах некоторым разумным значением (k*w*deltaT)
            double searchOutEdgesPathLimit = searchByContinuityPathLimitFactor*(averageSpeed*deltaTauSec(pos, prevProj.pos));

            int startEdgeId = prevProj.getRouteEdgeID();
            double startEdgeRestLength = prevProj.getRestLength();  // длина, котороую осталось пройти до конца ребра из предыдущей точки

            for (ProjOnEdge candidateProj : projsOnCandidateGeoms) {
                int goalEdgeId = candidateProj.getRouteEdgeID();
                // Ищем в графе путь из ребра start до ребра goal
                List<Integer> pathFromStartToGoalEdge = findPathFromStartToGoalEdge(startEdgeId, goalEdgeId, startEdgeRestLength, searchOutEdgesPathLimit);
                if (!pathFromStartToGoalEdge.isEmpty()) {  // если путь найден, то его последнее ребро - результат Map Matching'а
                    int pathLastEdgeId = pathFromStartToGoalEdge.get(pathFromStartToGoalEdge.size() - 1);
                    RouteEdge pathLastRouteEdge = createEdgeByEdgeID(pathLastEdgeId);
                    ProjOnEdge foundProjOnEdge = new ProjOnEdge(pathLastRouteEdge, pathLastEdgeId, pos, this::metric);
                    // сохраняем найденный путь (кроме первого ребра, т.к. оно не входит в список рёбер, пройденных "по непрерывности")
                    passedByContinuityEdgesList.addAll(pathFromStartToGoalEdge.subList(1, pathFromStartToGoalEdge.size()));
                    return Optional.of(new MMResult(foundProjOnEdge, 0.0));  // 0.0, т.к. пройденный путь далее вычисляется
                    // в методе getPassedByContinuityEdgesList()
                }
            }
            return Optional.empty();  // если прошли все "рёбра-кандидаты", и ничего не нашли, то возвращаем пустой результат
        } else {
            return Optional.empty();  // если нет дорог, проекция на которые имеет положительную метрику
        }
    }

    // Найти при помощи алгоритма A* путь от ребра start до ребра goal
    private List<Integer> findPathFromStartToGoalEdge(int startEdgeId, int goalEdgeId, double startEdgeRestLength, double searchInOutEdgesPathLimit) {
        this.goalEdgeId = goalEdgeId;
        closedEdges.clear();
        partitialSolutionsSet.clear();
        // Начальный путь содержин одно стартовое ребро
        Pair<List<Integer>, Double> startPathLength = new ImmutablePair<>(
            Collections.singletonList(startEdgeId), startEdgeRestLength);

        partitialSolutionsSet.offer(startPathLength);  // добавляем первое ребро

        int counter = 0;
        while (!partitialSolutionsSet.isEmpty()) {
            Pair<List<Integer>, Double> pathLength = partitialSolutionsSet.poll();  // путь с минимальной стоимостью

            List<Integer> path = pathLength.getLeft();
            double length = pathLength.getRight();

            int pathLastEdge = path.get(path.size() - 1);       // последнее ребро пути (оно же currentEdge)

            if (closedEdges.contains(pathLastEdge)) {
                continue;
            }
            if (pathLastEdge == goalEdgeId) {       // если последнее ребро пути равно целевому, то путь найден
                return path;
            }
            closedEdges.add(pathLastEdge);

            List<Integer> currentOutEdges = cacheFilesHandler.getOutEdgesByEdgeId(pathLastEdge);  // рёбра, исходящие из рассматриваемого
            for (int outEdgeId : currentOutEdges) {
                // Добавляем к текущему пути исходящее ребро
                List<Integer> pathToOutEdge = new ArrayList<>(path);
                pathToOutEdge.add(outEdgeId);

                // Находим длину до середины рассматриваемого ребра. Для этого прибавляем к предыдущей длине пути
                // половину длины последнего ребра (если последнее ребро не равно начальному) и половину длины рассматриваемого ребра
                double lengthToOutEdge = length;
                if (pathLastEdge != startEdgeId) {
                    lengthToOutEdge += cacheFilesHandler.getLengthByEdgeId(pathLastEdge)/2;
                }
                lengthToOutEdge += cacheFilesHandler.getLengthByEdgeId(outEdgeId)/2;

                if (lengthToOutEdge <= searchInOutEdgesPathLimit) {  // кладём новый путь в очередь, если он не длиннее диапазона поиска
                    partitialSolutionsSet.offer(new ImmutablePair<>(pathToOutEdge, lengthToOutEdge));
                }
            }
            counter++;
        }
        // если ничего не нашли, то возвращаем пустой список
        return Collections.emptyList();
    }

    @Override
    List<EdgeInfo> getPassedByContinuityEdgesList(MMResult mmResult, MMResult prevMMResult) {
        // Идём по списку пройденных рёбер (кроме последнего), и заполняем результирующий список объектами EdgeInfo
        List<EdgeInfo> passedByContinuityEdgeInfoList;
        if (passedByContinuityEdgesList.size() > 1) {
            passedByContinuityEdgeInfoList = passedByContinuityEdgesList.stream().limit(passedByContinuityEdgesList.size() - 1)
                .map(edgeId -> {
                    int geomId = cacheFilesHandler.getGeomId(edgeId);
                    RouteEdge routeEdge = createRouteEdgeFromCacheFiles(geomId);
                    return new EdgeInfo(
                        routeEdge.getOsmId(), routeEdge.getOsmVersion(), edgeId, routeEdge.getLength(), 1.0);
                }).collect(Collectors.toList());
        } else {
            passedByContinuityEdgeInfoList = new ArrayList<>();
        }

        // Для последнего ребра EdgeInfo берём из результата Map Matching'а
        passedByContinuityEdgeInfoList.add(mmResult.getEdgeInfo());

        // Общая длина пути, пройденная от точки до точки
        ProjOnEdge prevProj = prevMMResult.getProjOnEdge();
        ProjOnEdge lastProj = mmResult.getProjOnEdge();

        double traveledPathLength = prevProj.getRestLength();       // длина от предыдущей точки до конца первого ребра
        for (EdgeInfo edgeInfo : passedByContinuityEdgeInfoList) {  // добавляем длины остальных рёбер
            traveledPathLength += edgeInfo.getLength();
        }
        traveledPathLength -= lastProj.getRestLength();  // вычитаем длину пути, который осталось пройти до конца последнего ребра

        // Если в списке пройденных рёбер больше 1 ребра, то вычисляем для них аппроксимированное время прихода в начало ребра
        // В противном случае там будет одно время, взятое из MMResult
        if (passedByContinuityEdgeInfoList.size() > 1) {
            long prevTimestamp = prevProj.pos.getTimestamp();
            long currTimestamp = lastProj.pos.getTimestamp();
            long deltaTimestamp = currTimestamp - prevTimestamp;  // время движения между точками

            // Для промежуточных рёбер пути "по непрерывности" устанавливаем временные отметки прихода точки в начало ребра
            double pathLengthFromPrevToEdgeStart = prevProj.getRestLength();

            EdgeInfo firstPathByContinuityEdge = passedByContinuityEdgeInfoList.get(0);
            firstPathByContinuityEdge.setTimestamp(prevTimestamp + (long) ((pathLengthFromPrevToEdgeStart/traveledPathLength)*deltaTimestamp*1.0));

            for (int i = 1; i < passedByContinuityEdgeInfoList.size() - 1; i++) {
                EdgeInfo prevEdgeInfo = passedByContinuityEdgeInfoList.get(i - 1);
                pathLengthFromPrevToEdgeStart += prevEdgeInfo.getLength();
                EdgeInfo edgeInfo = passedByContinuityEdgeInfoList.get(i);
                edgeInfo.setTimestamp(prevTimestamp + (long) ((pathLengthFromPrevToEdgeStart/traveledPathLength)*deltaTimestamp*1.0));
            }
        }

        return passedByContinuityEdgeInfoList;
    }

    // Эвристическая функция стоимости для алгоритма A*
    private double heuristicCostFunction(Pair<List<Integer>, Double> pathLengthFromStartToCurrent, int goalEdgeId) {
        List<Integer> path = pathLengthFromStartToCurrent.getLeft();  // путь (id рёбер)
        int currentEdgeId = path.get(path.size() - 1);
        return heuristicCostFunction(pathLengthFromStartToCurrent, currentEdgeId, goalEdgeId);
    }

    private double heuristicCostFunction(Pair<List<Integer>, Double> pathLengthFromStartToCurrent, int currentEdgeId, int goalEdgeId) {
        return functionG(pathLengthFromStartToCurrent) + functionH(currentEdgeId, goalEdgeId);
    }

    // Функция стоимости достижения рассматриваемого ребра из начального
    private double functionG(Pair<List<Integer>, Double> pathLengthFromStartToCurrent) {
        return pathLengthFromStartToCurrent.getRight();  // длина пути от начальной точки до середины рассматриваемого ребра
    }

    // Функция эвристической оценки расстояния от рассматриваемого ребра до конечного
    // (возьмём расстояние по прямой от средней (для упрощения) точки рассматриваемого ребра
    // до средней точки конечного ребра)
    private double functionH(int currentEdgeId, int goalEdgeId) {
        int currentEdgeGeomId = cacheFilesHandler.getGeomId(currentEdgeId);
        int goalEdgeGeomId = cacheFilesHandler.getGeomId(goalEdgeId);

        Point3D currentEdgeMiddlePoint = cacheFilesHandler.getGeometryMiddlePointByGeomId(currentEdgeGeomId);
        Point3D goalEdgeMiddlePoint = cacheFilesHandler.getGeometryMiddlePointByGeomId(goalEdgeGeomId);

        return distanceBetweenTwo3Dpoints(currentEdgeMiddlePoint, goalEdgeMiddlePoint);
    }

}
