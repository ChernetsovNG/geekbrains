package ru.nchernetsov.sensors;

public class BaseSensor extends AbstractSensor {
    public BaseSensor(SensorImpl implementor) {
        super(implementor);
    }

    public double getValue() {
        return implementor.getValue();
    }
}
