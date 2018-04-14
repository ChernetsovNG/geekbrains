package ru.ncherenetsov.geo;

import java.util.Objects;

import static java.lang.Double.NaN;

/**
 * Географические координаты
 */
public class GeoPoint {
    /**
     * Долгота
     */
    private final double lon;
    /**
     * Широта
     */
    private final double lat;
    /**
     * Высота над уровнем моря, м
     */
    private final double alt;

    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        this.alt = NaN;
    }

    public GeoPoint(double lon, double lat, double alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getAlt() {
        return alt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPoint geoPoint = (GeoPoint) o;
        return Double.compare(geoPoint.lon, lon) == 0 &&
            Double.compare(geoPoint.lat, lat) == 0 &&
            Double.compare(geoPoint.alt, alt) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lon, lat, alt);
    }
}
