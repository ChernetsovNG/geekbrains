package ru.nchernetsov.drones;

import ru.nchernetsov.geo.GeoPoint;

public interface Drone {
    /**
     * Переместиться из одной точки в другую
     *
     * @param startPoint - начальная точка
     * @param endPoint   - конечная точка
     */
    void move(GeoPoint startPoint, GeoPoint endPoint);

    /**
     * Удерживать позицию
     *
     * @param position - позиция
     */
    void hold(GeoPoint position);
}
