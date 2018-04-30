package ru.nchernetsov.geometry;

/**
 * Географическая точка
 */
public class PointGeo {
    private final double lon;       //X, longitude, долгота, десятичные градусы
    private final double lat;       //Y, latitude, широта, десятичные градусы

    public PointGeo(double lon, double lat) {
        this.lon = longitudeWithRestriction(lon);
        this.lat = latitudeWithRestriction(lat);
    }

    private double latitudeWithRestriction(double latitude) {
        double result = 0.0;
        if (latitude >= -90.0 && latitude <= 90.0) {
            result = latitude;
        } else if (latitude > 90.0) {
            result = 180.0 - latitude;  //перешли через северный полюс и начали двигаться "вниз"
        } else if (latitude < -90.0) {
            result = -180.0 - latitude;  //перешли через южный полюс и начали двигаться "вверх"
        }
        return result;
    }

    private double longitudeWithRestriction(double longitude) {
        double result = 0.0;
        if (longitude >= -180.0 && longitude <= 180.0) {
            result = longitude;
        } else if (longitude > 180.0) {
            result = -360.0 + longitude;
        } else if (longitude < -180.0) {
            result = 360.0 + longitude;
        }
        return result;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "PointGeo{" +
            "lon=" + lon +
            ", lat=" + lat +
            '}';
    }
}
