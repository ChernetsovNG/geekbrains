package ru.nchernetsov.map_matching.cache;

import ru.nchernetsov.map_matching.RouteSegment;

import java.util.List;

/**
 * Геометрическая линия дорожного графа
 */
public class GeometryRecord {
    private final int startPointId;
    private final int endPointId;
    private final short segmentsCount;
    private final List<RouteSegment> segments;

    public GeometryRecord(int startPointId, int endPointId, short segmentsCount, List<RouteSegment> segments) {
        this.startPointId = startPointId;
        this.endPointId = endPointId;
        this.segmentsCount = segmentsCount;
        this.segments = segments;
    }

    public int getStartPointId() {
        return startPointId;
    }

    public int getEndPointId() {
        return endPointId;
    }

    public short getSegmentsCount() {
        return segmentsCount;
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return "GeometryRecord{" +
            "startPointId=" + startPointId +
            ", endPointId=" + endPointId +
            ", segmentsCount=" + segmentsCount +
            ", segments=" + segments +
            '}';
    }
}
