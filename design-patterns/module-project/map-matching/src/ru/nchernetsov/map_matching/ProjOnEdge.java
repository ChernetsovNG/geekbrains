package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.Pos3D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import static ru.nchernetsov.map_matching.ProjOnEdge.Direction.BACKWARD;
import static ru.nchernetsov.map_matching.ProjOnEdge.Direction.BOTH;
import static ru.nchernetsov.map_matching.ProjOnEdge.Direction.FORWARD;
import static ru.nchernetsov.map_matching.algorithm.MMAlgorithmCommon.maxMetricDegradationCount;

/**
 * Класс, отвечающий за расчёт проекции гео-точки на сегменты ребра,
 * (с учётом предыдущей проекции, если есть) и выбор наилучшей проекции
 */
public class ProjOnEdge {
    private static final Logger LOG = Logger.getLogger(ProjOnEdge.class.getName());

    public enum Direction {
        FORWARD,
        BACKWARD,
        BOTH
    }

    public final RouteEdge edge;
    public final Pos3D pos;
    private final List<ProjOnSegment> projOnSegmentsList = new ArrayList<>();
    public final int bestSegmentNum;  //номер сегмента ребра с максимальной метрикой
    public ProjOnEdge parentProj = null;
    public final boolean isForwardMovement;  //движение по ребру - вперёд?
    private final double relPosOnEdge;  //относительное положение от начала ребра до проекции точки на "лучший" сегмент (или до конца "лучшего сегмента)
    public double pathTraveledByContinuity = 0.0;  //путь, пройденный по непрерывности
    public long metricDegradation = maxMetricDegradationCount;

    public ProjOnEdge(RouteEdge edge, Pos3D pos, BiFunction<Double, Double, Double> calcMetricFunction) {
        this.edge = edge;
        this.pos = pos;
        for (RouteSegment segment : edge.getSegments()) {
            projOnSegmentsList.add(new ProjOnSegment(segment, pos, edge.isOneWay() ? FORWARD : BOTH, calcMetricFunction));
        }
        bestSegmentNum = getBestSegmentNum();
        relPosOnEdge = getRelPosOnEdge();
        //TODO - здесь предполагается, что если дорога односторонняя - то движение вперёд
        isForwardMovement = edge.isOneWay() || projOnSegmentsList.get(bestSegmentNum).correlation > 0;
    }

    public ProjOnEdge(RouteEdge edge, Integer routeEdgeID, Pos3D pos, BiFunction<Double, Double, Double> calcMetricFunction) {
        this.edge = edge;
        this.pos = pos;
        for (RouteSegment seg : edge.getSegments()) {
            projOnSegmentsList.add(new ProjOnSegment(seg, pos, edge.getForwardID() == routeEdgeID ? FORWARD : BACKWARD, calcMetricFunction));
        }
        bestSegmentNum = getBestSegmentNum();
        relPosOnEdge = getRelPosOnEdge();
        isForwardMovement = (edge.getForwardID() == routeEdgeID);
    }

    public int getRouteEdgeID() {
        return isForwardMovement ? edge.getForwardID() : edge.getBackwardID();
    }

    public long getRouteEdgeOsmId() {
        return edge.getOsmId();
    }

    public int getRouteEdgeOsmVersion() {
        return edge.getOsmVersion();
    }

    //Относительная длина от начала ребра до проекции точки на "лучший" сегмент
    public double getRelPosOnEdge() {
        double posOnEdge = 0.0;
        try {
            for (int i = 0; i < bestSegmentNum; i++)
                posOnEdge += edge.getSegments().get(i).getLength();

            ProjOnSegment projOnBestSegment = projOnSegmentsList.get(bestSegmentNum);
            if (projOnBestSegment.isPointOutRight()) {
                posOnEdge += projOnBestSegment.length;
            } else if (projOnBestSegment.isPointInside()) {
                posOnEdge += projOnBestSegment.vecFromBegSegmentToPosProjOnSegment;
            }

            return posOnEdge / edge.getLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getDistanceToBestSegment() {
        return projOnSegmentsList.get(bestSegmentNum).distance;
    }

    public double getCorrelation() {
        return projOnSegmentsList.get(bestSegmentNum).correlation;
    }

    public double getBestSegmentMetric() {
        return projOnSegmentsList.get(bestSegmentNum).metric;
    }

    public boolean isProjInsideEdge() {
        return isPointExactlyBetweenSegments() || isPointInsideOneOfTheEdgeSegments();
    }

    public double getPassedLength() {
        return edge.getLength() * (isForwardMovement ? relPosOnEdge : (1 - relPosOnEdge));
    }

    public double getRestLength() {
        return edge.getLength() * (isForwardMovement ? (1 - relPosOnEdge) : relPosOnEdge);
    }

    public List<Integer> outEdges() {
        return new ArrayList<>(getOutEdgeIDs());
        // return getOutEdgeIDs().stream().map(id -> id + 1).collect(Collectors.toList());  // TODO: проверить это +1
    }

    private List<Integer> getOutEdgeIDs() {
        return isForwardMovement ? edge.getForwardOutEdges() : edge.getBackwardOutEdges();
    }

    //Находит номер сегмента с максимальной метрикой
    private int getBestSegmentNum() {
        if (isPointInsideOneOfTheEdgeSegments()) {
            double maxProjMetric = -Double.MAX_VALUE;
            int i = 0;
            int i0 = -1;
            for (ProjOnSegment projOnSegment : projOnSegmentsList) {
                if (projOnSegment.isPointInside() && projOnSegment.metric > maxProjMetric) {
                    maxProjMetric = projOnSegment.metric;
                    i0 = i;
                }
                i++;
            }
            return i0;
        } else if (isPointExactlyBetweenSegments()) {
            for (int i = 1; i < projOnSegmentsList.size(); i++) {
                if (projOnSegmentsList.get(i - 1).isPointOutRight() && projOnSegmentsList.get(i).isPointOutLeft()) {
                    return projOnSegmentsList.get(i - 1).metric > projOnSegmentsList.get(i).metric ? i - 1 : i;
                }
            }
        } else {  //точка слева или справа от ребра
            return projOnSegmentsList.get(0).metric > projOnSegmentsList.get(projOnSegmentsList.size() - 1).metric ?
                0 : projOnSegmentsList.size() - 1;
        }
        throw new RuntimeException("Exception in method getBestSegmentNumber. Position of projection is undefinded");
    }

    private boolean isPointInsideOneOfTheEdgeSegments() {
        for (ProjOnSegment projOnSegment : projOnSegmentsList) {
            if (projOnSegment.isPointInside()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointExactlyBetweenSegments() {
        if (!isPointInsideOneOfTheEdgeSegments() && projOnSegmentsList.size() > 1) {
            for (int i = 1; i < projOnSegmentsList.size(); i++) {
                if (projOnSegmentsList.get(i - 1).isPointOutRight() && projOnSegmentsList.get(i).isPointOutLeft()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int compare(ProjOnEdge p0, ProjOnEdge p1) {
        int metricCMP = Double.compare(p1.getBestSegmentMetric(), p0.getBestSegmentMetric());
        if (metricCMP == 0) {
            return Double.compare(p0.pathTraveledByContinuity, p1.pathTraveledByContinuity);
        }
        return metricCMP;
    }

    public boolean isProjMetricBetterThan(ProjOnEdge another) {
        return getBestSegmentMetric() >= another.getBestSegmentMetric();
    }

    public boolean hasPositiveMetric() {
        return getBestSegmentMetric() > 0;
    }

    // вычислить длину пути от проекции точки до конца ребра
    public double calcPathFromProjToEndEdge() {
        return (isForwardMovement ? (1.0 - relPosOnEdge) : relPosOnEdge) * edge.getLength();
    }

}