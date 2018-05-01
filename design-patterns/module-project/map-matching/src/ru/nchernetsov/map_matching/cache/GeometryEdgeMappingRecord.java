package ru.nchernetsov.map_matching.cache;

/**
 * Информация о соответствии линии и рёбер дорожного графа
 */
public class GeometryEdgeMappingRecord {
    private final long osmId;
    private final int osmVersion;
    private final int backwardID;
    private final int forwardID;
    private final long geometryPos;
    private final long latLonPos;
    private final float length;

    public GeometryEdgeMappingRecord(long osmId, int osmVersion, int backwardID, int forwardID, long geometryPos, long latLonPos, float length) {
        this.osmId = osmId;
        this.osmVersion = osmVersion;
        this.backwardID = backwardID;
        this.forwardID = forwardID;
        this.geometryPos = geometryPos;
        this.latLonPos = latLonPos;
        this.length = length;
    }

    public long getOsmId() {
        return osmId;
    }

    public int getOsmVersion() {
        return osmVersion;
    }

    public int getBackwardID() {
        return backwardID;
    }

    public int getForwardID() {
        return forwardID;
    }

    public long getGeometryPos() {
        return geometryPos;
    }

    public long getLatLonPos() {
        return latLonPos;
    }

    public float getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "GeometryEdgeMappingRecord{" +
            "osmId=" + osmId +
            ", osmVersion=" + osmVersion +
            ", backwardID=" + backwardID +
            ", forwardID=" + forwardID +
            ", geometryPos=" + geometryPos +
            ", latLonPos=" + latLonPos +
            ", length=" + length +
            '}';
    }
}
