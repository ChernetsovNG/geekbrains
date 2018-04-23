package ru.nchernetsov.sensors;

public interface SensorImpl {
    double getValue();

    void stopMeasure();
}
