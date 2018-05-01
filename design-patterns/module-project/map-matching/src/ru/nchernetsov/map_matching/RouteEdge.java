package ru.nchernetsov.map_matching;

import java.util.List;

/**
 * Ребра дорожного графа, соответствующие дороге, а также исходящие рёбра для рёбер этой дороги
 */
public class RouteEdge {
    private final int geomID;
    private final long osmId;
    private final int osmVersion;
    private final int forwardID;
    private final int backwardID;
    private float length;
    private final List<RouteSegment> segments;
    private final List<Integer> forwardOutEdges;
    private final List<Integer> backwardOutEdges;

    public RouteEdge(int geomID, long osmId, int osmVersion, int forwardID, int backwardID, float length, List<RouteSegment> segments,
                     List<Integer> forwardOutEdges, List<Integer> backwardOutEdges) {
        this.geomID = geomID;
        this.osmId = osmId;
        this.osmVersion = osmVersion;
        this.forwardID = forwardID;
        this.backwardID = backwardID;
        this.length = length;
        this.segments = segments;
        this.forwardOutEdges = forwardOutEdges;
        this.backwardOutEdges = backwardOutEdges;
    }

    public boolean isOneWay() {
        return backwardID == 0;
    }

    public int getGeomID() {
        return geomID;
    }

    public long getOsmId() {
        return osmId;
    }

    public int getOsmVersion() {
        return osmVersion;
    }

    public int getForwardID() {
        return forwardID;
    }

    public int getBackwardID() {
        return backwardID;
    }

    public float getLength() {
        return length;
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    public List<Integer> getForwardOutEdges() {
        return forwardOutEdges;
    }

    public List<Integer> getBackwardOutEdges() {
        return backwardOutEdges;
    }
}