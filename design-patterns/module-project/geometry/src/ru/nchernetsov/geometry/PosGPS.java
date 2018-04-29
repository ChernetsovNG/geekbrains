package ru.nchernetsov.geometry;

/**
 * Расширение PosGEO для представления GPS-координаты
 */
public class PosGPS extends PosGEO {
    private double azimuth = 0.0;  // азимут (угол отклонения от направления на север)
    private double speed = 0.0;    // скорость, м/с

    public PosGPS(long timestamp, double lat, double lon, double altitude, double azimuth, double speed) {
        super(timestamp,lat,lon,altitude);
        this.azimuth = azimuth;
        this.speed  = speed;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getSpeed() {
        return speed;
    }

    public String toString() {
        return super.toString() + "," + azimuth + "," + speed;
    }
}
