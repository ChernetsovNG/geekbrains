package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.Pos3D;

/**
 * Результат Map Matching'а
 */
public class MMResult {
    public final ProjOnEdge projOnEdge;
    private double traveledOnGraphLength;  // путь, пройденный по графу от предыдущей точки

    public MMResult(ProjOnEdge proj, double traveledOnGraphLength) {
        this.projOnEdge = proj;
        this.traveledOnGraphLength = traveledOnGraphLength;
    }

    public EdgeInfo getEdgeInfo() {
        EdgeInfo edgeInfo = new EdgeInfo(
            projOnEdge.getRouteEdgeOsmId(),
            projOnEdge.getRouteEdgeOsmVersion(),
            projOnEdge.getRouteEdgeID(),
            projOnEdge.edge.getLength(),
            projOnEdge.pos.getTimestamp(),
            projOnEdge.getRelPosOnEdge());
        return edgeInfo;
    }

    public ProjOnEdge getProjOnEdge() {
        return projOnEdge;
    }

    public double getDistanceToBestSegment() {
        return projOnEdge.getDistanceToBestSegment();
    }

    public int getRouteEdgeID() {
        return projOnEdge.getRouteEdgeID();
    }

    public long getRouteEdgeOsmId() {
        return projOnEdge.getRouteEdgeOsmId();
    }

    public int getRouteEdgeOsmVersion() {
        return projOnEdge.getRouteEdgeOsmVersion();
    }

    public double getRouteEdgeLength() {
        return projOnEdge.edge.getLength();
    }

    public double getRelPosOnEdge() {
        return projOnEdge.getRelPosOnEdge();
    }

    public long getTimestamp() {
        return projOnEdge.pos.getTimestamp();
    }

    public Pos3D getPos3D() {
        return projOnEdge.pos;
    }

    public double getTraveledOnGraphLength() {
        return traveledOnGraphLength;
    }

    public void setTraveledOnGraphLength(double traveledOnGraphLength) {
        this.traveledOnGraphLength = traveledOnGraphLength;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        ProjOnEdge p = getProjOnEdge();
        while (p != null) {
            str.append("EdgeID: ").append(p.getRouteEdgeID()).append("; ")
                .append("Edge length: ").append(p.edge.getLength()).append("; ")
                .append("isForwardMovement: ").append(p.isForwardMovement).append("; ")
                .append("best segment: ").append(p.bestSegmentNum).append("; ")
                .append("is inside: ").append(p.isProjInsideEdge()).append("; ")
                .append("position on edge: ").append(p.getRelPosOnEdge()).append("; ")
                .append("distance: ").append(p.getDistanceToBestSegment()).append("; ")
                .append("correlation: ").append(p.getCorrelation()).append("; ")
                .append("metric: ").append(p.getBestSegmentMetric()).append(" ");
            p = p.parentProj;
            if (p != null) {
                str.append(" <- ");
            }
        }
        return str.toString();
    }
}
