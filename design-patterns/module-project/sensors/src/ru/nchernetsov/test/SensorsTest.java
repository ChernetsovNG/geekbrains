package ru.nchernetsov.test;

import ru.nchernetsov.sensors.AverageSensor;
import ru.nchernetsov.sensors.BaseSensor;
import ru.nchernetsov.sensors.SensorImpl;
import ru.nchernetsov.sensors.mock.MockAccelerationSensor;
import ru.nchernetsov.sensors.mock.MockSpeedSensor;

import java.util.concurrent.TimeUnit;

public class SensorsTest {

    public static void main(String[] args) {
        MockSpeedSensor speedSensor = new MockSpeedSensor(2);
        MockAccelerationSensor accelerationSensor = new MockAccelerationSensor(1);

        measure(speedSensor);
        measure(accelerationSensor);
    }

    private static void measure(SensorImpl sensor) {
        System.out.println(sensor.getClass().getName());

        BaseSensor baseSensor = new BaseSensor(sensor);
        AverageSensor averageSensor = new AverageSensor(sensor, 5);

        for (int i = 0; i < 10; i++) {
            System.out.printf("Мгновенное значение: %f, среднее значение: %f \n",
                baseSensor.getValue(), averageSensor.getAverageValue());
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sensor.stopMeasure();
    }
}
