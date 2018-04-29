package ru.nchernetsov.geometry;

import java.time.Instant;

/**
 * Базовое представление геокоординаты
 */
public class PosGEO extends PointGeo {
    private final long timestamp;     //временная отметка, UNIX-time
    private final double altitude;    //высота (над уровнем моря?), м

    public PosGEO(long timestamp, double lat, double lon, double altitude) {
        super(lon, lat);
        this.timestamp = timestamp;
        if (altitude >= -10000 && altitude <= 100000) {
            this.altitude = altitude;
        } else {
            this.altitude = 0.0;
        }
    }

    public String toString() {
        return Instant.ofEpochMilli(timestamp).toString() + " " + getLat() + "," + getLon() + "," + altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getAltitude() {
        return altitude;
    }
}
