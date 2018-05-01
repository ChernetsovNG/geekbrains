package ru.nchernetsov.map_matching;

import java.time.Instant;

/**
 * Информация о проекции на ребро дорожного графа
 */
public class EdgeInfo {
    private final long osmId;
    private final int version;
    private final int id;
    private final double length;
    private long timestamp;
    private double relPosOnEdge;

    public EdgeInfo(long osmId, int version, int id, double length, double relPosOnEdge) {
        this.osmId = osmId;
        this.version = version;
        this.id = id;
        this.length = length;
        this.relPosOnEdge = relPosOnEdge;
    }

    public EdgeInfo(long osmId, int version, int id, double length, long timestamp, double relPosOnEdge) {
        this.osmId = osmId;
        this.version = version;
        this.id = id;
        this.length = length;
        this.timestamp = timestamp;
        this.relPosOnEdge = relPosOnEdge;
    }

    public long getOsmId() {
        return osmId;
    }

    public int getVersion() {
        return version;
    }

    public int getId() {
        return id;
    }

    public double getLength() {
        return length;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getRelPosOnEdge() {
        return relPosOnEdge;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRelPosOnEdge(double relPosOnEdge) {
        this.relPosOnEdge = relPosOnEdge;
    }

    public String toString() {
        return id + " ; " + Instant.ofEpochMilli(timestamp);
    }
}
