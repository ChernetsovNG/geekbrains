package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.PosGPS;

/**
 * Запрос на выполнение Map Matching для точки gps и box определённых размеров
 */
public class MMRequest {
    private final PosGPS gps;
    private final double boxHalfWidth;
    private final double boxHalfHeight;

    public MMRequest(PosGPS gps, double boxHalfWidth, double boxHalfHeight) {
        this.gps = gps;
        this.boxHalfWidth = boxHalfWidth;
        this.boxHalfHeight = boxHalfHeight;
    }

    public PosGPS getGps() {
        return gps;
    }

    public double getBoxHalfWidth() {
        return boxHalfWidth;
    }

    public double getBoxHalfHeight() {
        return boxHalfHeight;
    }
}
