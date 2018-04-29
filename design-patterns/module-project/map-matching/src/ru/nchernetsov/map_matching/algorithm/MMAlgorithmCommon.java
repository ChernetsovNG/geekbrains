package ru.nchernetsov.map_matching.algorithm;

import ru.nchernetsov.geometry.FromGeoTo3DTransformer;
import ru.nchernetsov.geometry.Pos3D;
import ru.nchernetsov.geometry.PosGPS;
import ru.nchernetsov.map_matching.*;
import ru.nchernetsov.map_matching.cache.CacheFilesHandler;
import ru.nchernetsov.map_matching.cache.GeometryEdgeMappingRecord;
import ru.nchernetsov.map_matching.cache.GeometryRecord;
import ru.nchernetsov.map_matching.cache.GraphRecord;
import ru.nchernetsov.map_matching.interfaces.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.pow;
import static ru.nchernetsov.geometry.GeometryOperation.distanceBetweenTwo3Dpoints;
import static ru.nchernetsov.map_matching.algorithm.MMAlgorithmConfig.*;

/**
 * Общие методы для разных реализаций алгоритма Map Matching'а
 */
public abstract class MMAlgorithmCommon {
    final CacheFilesHandler cacheFilesHandler;

    static long bufferSize;
    public static long maxMetricDegradationCount;
    static long maxRecursiveCallCount;  // максимальное количество рекурсивных вызовов
    private static double zeroSpeedLimit;
    private double normalSpeedSearchBoxSize;
    private double zeroSpeedSearchBoxSize;

    private double searchByContinuityRadiusFactor;
    private long deltaTauContinuityLimitMs;
    double searchByContinuityPathLimitFactor;

    private double muD, nD;
    private double muAlpha, nAlpha;
    private double a;

    private double nearestBoxHalfSize = 250.0f;
    private boolean considerCourse = true;  //учитывать ли курс при выборе ребра в алгоритме Map Matching'а?

    MMAlgorithmCommon(CacheFilesHandler cacheFilesHandler, Map<String, Object> algorithmConfig) {
        this.cacheFilesHandler = cacheFilesHandler;
        loadAlgorithmParams(algorithmConfig);
    }

    private void loadAlgorithmParams(Map<String, Object> algorithmConfig) {
        maxMetricDegradationCount = (long) algorithmConfig.get(MAX_METRIC_DEGRADATION_COUNT);
        maxRecursiveCallCount = (long) algorithmConfig.get(MAX_RECURSIVE_CALL_COUNT);
        zeroSpeedLimit = (double) algorithmConfig.get(ZERO_SPEED_LIMIT);
        normalSpeedSearchBoxSize = (double) algorithmConfig.get(NORMAL_SPEED_SEARCH_BOX_SIZE);
        zeroSpeedSearchBoxSize = (double) algorithmConfig.get(ZERO_SPEED_SEARCH_BOX_SIZE);

        searchByContinuityRadiusFactor = (double) algorithmConfig.get(SEARCH_BY_CONTINUITY_RADIUS_FACTOR);
        deltaTauContinuityLimitMs = (long) algorithmConfig.get(DELTA_TAU_CONTINUITY_LIMIT_MS);
        searchByContinuityPathLimitFactor = (double) algorithmConfig.get(SEARCH_BY_CONTINUITY_PATH_LIMIT_FACTOR);

        muD = (double) algorithmConfig.get(MMAlgorithmConfig.muD);
        muAlpha = (double) algorithmConfig.get(MMAlgorithmConfig.muAlpha);
        nD = (double) algorithmConfig.get(MMAlgorithmConfig.nD);
        nAlpha = (double) algorithmConfig.get(MMAlgorithmConfig.nAlpha);
        a = (double) algorithmConfig.get(MMAlgorithmConfig.a);
    }

    // Общая часть методов applyMM для разных алгоритмов. Возвращаем вычисленный результат Map Matching'а
    Optional<MMResult> commonPartOfMapMatchingAlgorithm(long deviceId, PosGPS gps, DevicesMMResultsHolder deviceMMResultsHolder, ActionOnTransition onTransition, ActionOnShift onShift) {
        long timestamp = gps.getTimestamp();

        DeviceMMResults deviceMMResults = deviceMMResultsHolder.getByID(deviceId);

        setupBoxSizeBySpeed(gps.getSpeed(), normalSpeedSearchBoxSize, zeroSpeedSearchBoxSize);

        FromGeoTo3DTransformer transformer = new FromGeoTo3DTransformer(gps.getLat(), gps.getLon());
        Pos3D pos3D = Pos3D.fromPosGPS(transformer, gps);
        double[] halfSizeBoxInDegrees = transformer.makeBoxHalfSizeInDegrees(nearestBoxHalfSize);

        deviceMMResults.addMMRequest(new MMRequest(gps, halfSizeBoxInDegrees[0], halfSizeBoxInDegrees[1]));

        Optional<MMResult> mmResult = Optional.empty();
        Optional<MMResult> prevMMResult;

        boolean itIsTheNewSearch = false;
        if (deviceMMResults.isEmpty()) {  // если нет сохранённых результатов Map Matching'а
            itIsTheNewSearch = true;
        } else {  // если есть сохранённые результаты
            Optional<MMResult> lastMMResult = deviceMMResults.getLastMMResult();
            if (!lastMMResult.isPresent()) {  // если предыдущий результат был пустой
                itIsTheNewSearch = true;
            } else if (!canAssumeContinuity(lastMMResult.get().getPos3D(), pos3D)) {
                itIsTheNewSearch = true;
            } else {  // если есть предыдущий результат Map Matching'а и мы можем делать расчёт "по непрерывности"
                prevMMResult = lastMMResult;
                mmResult = projOnEdgeByContinuity(prevMMResult.get(), gps, pos3D);
                if (!mmResult.isPresent()) {
                    itIsTheNewSearch = true;
                }
            }
        }

        // Поиск "с нуля" на основе R*-дерева
        if (itIsTheNewSearch) {
            prevMMResult = Optional.empty();
            mmResult = findMMResultByBestProjInRTree(gps, pos3D, halfSizeBoxInDegrees);
        } else {
            prevMMResult = deviceMMResults.getLastMMResult();
        }

        if (!prevMMResult.isPresent() && mmResult.isPresent()) {
            transitionFromNullToEdge(mmResult.get(), onTransition);
        } else if (prevMMResult.isPresent()) {
            int currEdgeId = mmResult.get().getRouteEdgeID();
            int prevEdgeId = prevMMResult.get().getRouteEdgeID();
            if (currEdgeId != prevEdgeId) {
                transitionOnAnotherEdge(mmResult.get(), prevMMResult.get(), onTransition);
            } else {
                shiftOnEdge(mmResult.get(), prevMMResult.get(), onShift);
            }
        } else {
            // ребро не определено
            if (onShift != null)
                onShift.execute(null);
        }

        deviceMMResults.addMMResult(mmResult);
        deviceMMResultsHolder.save(deviceId, deviceMMResults);

        return mmResult;
    }

    private void transitionFromNullToEdge(MMResult mmResult, ActionOnTransition onTransition) {
        EdgeInfo edgeInfo = new EdgeInfo(
            mmResult.getRouteEdgeOsmId(),
            mmResult.getRouteEdgeOsmVersion(),
            mmResult.getRouteEdgeID(),
            mmResult.getRouteEdgeLength(),
            mmResult.getTimestamp(),
            mmResult.getRelPosOnEdge());

        mmResult.setTraveledOnGraphLength(0.0);

        if (onTransition != null) {
            onTransition.execute(edgeInfo, null);
        }
    }

    private void shiftOnEdge(MMResult mmResult, MMResult prevMMResult, ActionOnShift onShift) {
        EdgeInfo edgeInfo = new EdgeInfo(
            mmResult.getRouteEdgeOsmId(),
            mmResult.getRouteEdgeOsmVersion(),
            mmResult.getRouteEdgeID(),
            mmResult.getRouteEdgeLength(),
            mmResult.getTimestamp(),
            mmResult.getRelPosOnEdge());

        // Ставим модуль, т.к. относительная позиция на ребре может как увеличиваться, так и уменьшаться
        double traveledOnGraphPath = edgeInfo.getLength() * abs(mmResult.getRelPosOnEdge() - prevMMResult.getRelPosOnEdge());
        mmResult.setTraveledOnGraphLength(traveledOnGraphPath);

        if (onShift != null) {
            onShift.execute(edgeInfo);
        }
    }

    private void transitionOnAnotherEdge(MMResult mmResult, MMResult prevMMResult, ActionOnTransition onTransition) {
        // список рёбер, пройденных "по непрерывности"
        List<EdgeInfo> passedByContinuityEdgesList = getPassedByContinuityEdgesList(mmResult, prevMMResult);
        EdgeInfo edgeInfo = mmResult.getEdgeInfo();

        double traveledOnGraphPath = getTraveledPathLength(mmResult, prevMMResult, passedByContinuityEdgesList);
        mmResult.setTraveledOnGraphLength(traveledOnGraphPath);

        if (onTransition != null) {
            onTransition.execute(edgeInfo, passedByContinuityEdgesList);
        }
    }

    //Можно ли "делать расчёт в предположении непрерывности"
    private boolean canAssumeContinuity(Pos3D previous, Pos3D current) {
        if (previous.getTimestamp() > current.getTimestamp()) {  // позиция старше предыдущей
            return false;
        } else if (current.getTimestamp() - previous.getTimestamp() > deltaTauContinuityLimitMs) {  // точки далеко отстоят друг от друга
            return false;
        } else {
            double distance = distanceBetweenTwo3Dpoints(previous, current);  // расстояние между 3D точками
            double expected = getExpectedDistanceByAverageSpeed(current, previous);  // ожидаемая дистанция
            return distance < searchByContinuityRadiusFactor * expected;
        }
    }

    private void setupBoxSizeBySpeed(double speed, double normalBoxSize, double zeroSpeedBoxSize) {
        if (speed < zeroSpeedLimit) {
            considerCourse = false;  // на малых скоростях не принимаем курс во внимание при выборе ребра
            nearestBoxHalfSize = zeroSpeedBoxSize;
        } else {
            considerCourse = true;  // скорость хорошая, курс используем
            nearestBoxHalfSize = normalBoxSize;
        }
    }

    // Поиск "с нуля" на основе R*-дерева
    private Optional<MMResult> findMMResultByBestProjInRTree(PosGPS gps, Pos3D pos3D, double[] halfSizeBoxInDegrees) {
        Optional<MMResult> mmResult = Optional.empty();
        List<Integer> geomsFoundInBox = findGeomsInRTree(gps, halfSizeBoxInDegrees);
        if (geomsFoundInBox.size() > 0) {
            Optional<ProjOnEdge> bestProj = selectBestProj(geomsFoundInBox, pos3D);
            if (bestProj.isPresent()) {
                MMResult mmResultByBestProj = new MMResult(bestProj.get(), 0.0);
                mmResult = Optional.of(mmResultByBestProj);
                if (objectIsOutOfBox(mmResultByBestProj.getDistanceToBestSegment())) {
                    mmResult = Optional.empty();
                }
            }
            geomsFoundInBox.clear();
        }
        return mmResult;
    }

    // Найти линии в графе, попадающие в прямоугольную область вокру точки
    // Задаётся "полуразмер" области поиска по долготе и по широте
    List<Integer> findGeomsInRTree(PosGPS gps, double[] halfSizeBoxInDegrees) {
        // Поиск "с нуля" на основе R*-дерева
        double minX = gps.getLon() - halfSizeBoxInDegrees[0];
        double minY = gps.getLat() - halfSizeBoxInDegrees[1];
        double maxX = gps.getLon() + halfSizeBoxInDegrees[0];
        double maxY = gps.getLat() + halfSizeBoxInDegrees[1];
        return cacheFilesHandler.rTreeSearch(minX, minY, maxX, maxY);
    }

    RouteEdge createEdgeByEdgeID(Integer routeEdgeID) {
        int geomID = cacheFilesHandler.getGeomId(routeEdgeID);
        return createRouteEdgeFromCacheFiles(geomID);
    }

    double metric(double distance, double correlation) {
        double Sd = muD - a * pow(distance, nD);  // составляющая Sd учитывает взвешенное расстояние от точки трека до ребра
        double Sa = considerCourse ? muAlpha * pow(correlation, nAlpha) : 0;  // составляющая Sa учитывает угол между направлением геометрии ребра и направлением вектора скорости
        return (Sd + Sa);
    }

    private boolean objectIsOutOfBox(double distance) {
        return distance > nearestBoxHalfSize;
    }

    RouteEdge createRouteEdgeFromCacheFiles(int geomID) {
        GeometryEdgeMappingRecord geometryEdgeMappingRecord = cacheFilesHandler.getGeometryEdgeMappingRecord(geomID);

        long osmId = geometryEdgeMappingRecord.getOsmId();
        int osmVersion = geometryEdgeMappingRecord.getOsmVersion();
        long geometryPos = geometryEdgeMappingRecord.getGeometryPos();
        int forwardId = geometryEdgeMappingRecord.getForwardID();
        int backwardId = geometryEdgeMappingRecord.getBackwardID();

        GeometryRecord geometryRecord = cacheFilesHandler.getGeometryRecordByGeometryPos(geometryPos);
        GraphRecord graphRecord = cacheFilesHandler.getGraphRecord(forwardId, backwardId);

        return new RouteEdge(geomID, osmId, osmVersion, forwardId, backwardId, geometryEdgeMappingRecord.getLength(),
            geometryRecord.getSegments(), graphRecord.getForwardOutEdges(), graphRecord.getBackwardOutEdges());
    }

    // Рассчитываем расстояние, на котором метрика потенциально может быть положительной
    // для всех линий, лежащих от точки на большем расстоянии, метрика гарантированно будет отрицательной
    double findDistanceCanBePositiveMetric() {
        return pow(muD / a, 1.0 / nD) + muAlpha;
    }

    boolean isSlowMotionAgainstDirection(ProjOnEdge projOnEdge, ProjOnEdge newProjOnPrevEdge) {
        double relPosOnEdge = projOnEdge.getRelPosOnEdge();
        double newRelPosOnEdge = newProjOnPrevEdge.getRelPosOnEdge();
        if (projOnEdge.pos.getSpeed() < zeroSpeedLimit) {
            if (projOnEdge.isForwardMovement && newRelPosOnEdge < relPosOnEdge) {
                return true;
            }
            if (!projOnEdge.isForwardMovement && newRelPosOnEdge > relPosOnEdge) {
                return true;
            }
        }
        return false;
    }

    private double getExpectedDistanceByAverageSpeed(Pos3D pos, Pos3D oldPos) {
        double averageSpeed = (pos.getSpeed() + oldPos.getSpeed()) / 2;  // Средняя скорость по двум точкам
        return averageSpeed * deltaTauSec(pos, oldPos);
    }

    double deltaTauSec(Pos3D pos, Pos3D oldPos) {
        return (pos.getTimestamp() - oldPos.getTimestamp()) * 0.001;
    }

    // Выбрать проекцию с наилучшей метрикой на дороги из списка
    private Optional<ProjOnEdge> selectBestProj(List<Integer> geomIDs, Pos3D pos) {
        List<ProjOnEdge> projOnEdgesList = selectProjsWithPositiveMetric(geomIDs, pos);
        if (!projOnEdgesList.isEmpty()) {
            ProjOnEdge bestProj = projOnEdgesList.get(0);  // т.к. список уже отсортирован по метрике
            projOnEdgesList.clear();
            return Optional.of(bestProj);
        } else {
            return Optional.empty();
        }
    }

    // выбрать из списка дорог такие, метрика для которых положительна
    // вернуть список проекций на эти дороги, отсортированный по убыванию метрики
    List<ProjOnEdge> selectProjsWithPositiveMetric(List<Integer> geomIDs, Pos3D pos) {
        try {
            return geomIDs.stream()
                .map(geomId -> {
                    RouteEdge routeEdge = createRouteEdgeFromCacheFiles(geomId);
                    return new ProjOnEdge(routeEdge, pos, this::metric);
                })
                .filter(ProjOnEdge::hasPositiveMetric)
                .sorted((p0, p1) -> Double.compare(p1.getBestSegmentMetric(), p0.getBestSegmentMetric()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Общая длина пути, пройденная от точки до точки
    private double getTraveledPathLength(MMResult mmResult, MMResult prevMMResult, List<EdgeInfo> passedByContinuityEdgeInfoList) {
        ProjOnEdge prevProj = prevMMResult.getProjOnEdge();
        ProjOnEdge lastProj = mmResult.getProjOnEdge();

        double traveledPathLength;

        // Если мы имеем переход с ребра на ребро, и эти два ребра относятся к одной двухсторонней дороге, то это
        // означает, что изменилось направление движения и мы поехали в другую сторону. В этом случае мы не считаем путь
        // по графу, а определяем смещение от точки до точки
        if (passedByContinuityEdgeInfoList.size() == 1 && prevProj.edge.getGeomID() == lastProj.edge.getGeomID()) {
            traveledPathLength = abs(prevProj.getPassedLength() - lastProj.getRestLength());
        } else {
            traveledPathLength = prevProj.getRestLength();       // длина от предыдущей точки до конца первого ребра
            for (EdgeInfo edgeInfo : passedByContinuityEdgeInfoList) {  // добавляем длины остальных рёбер
                traveledPathLength += edgeInfo.getLength();
            }
            traveledPathLength -= lastProj.getRestLength();  // вычитаем длину пути, который осталось пройти до конца последнего ребра
        }

        return traveledPathLength;
    }

    abstract Optional<MMResult> projOnEdgeByContinuity(MMResult prevMMResult, PosGPS gps, Pos3D pos);

    abstract List<EdgeInfo> getPassedByContinuityEdgesList(MMResult mmResult, MMResult prevMMResult);
}
