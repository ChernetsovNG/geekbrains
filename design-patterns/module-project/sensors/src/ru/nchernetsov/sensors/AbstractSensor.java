package ru.nchernetsov.sensors;

public abstract class AbstractSensor {
    SensorImpl implementor;

    public AbstractSensor(SensorImpl implementor) {
        this.implementor = implementor;
    }
}
