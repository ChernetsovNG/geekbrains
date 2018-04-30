package ru.nchernetsov.sensors.mock;

import ru.nchernetsov.sensors.SensorImpl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ru.nchernetsov.sensors.mock.Utils.sleepAndGenerateRandomValue;

public class MockAccelerationSensor implements SensorImpl {
    private static final double ACCELERATION_MIN = -10.0;
    private static final double ACCELERATION_MAX = +10.0;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final double valueGenerationFrequency;
    private final Future<?> future;

    private volatile double currentValue;  // Мгновенное значение ускорения

    public MockAccelerationSensor(double valueGenerationFrequency) {
        this.valueGenerationFrequency = valueGenerationFrequency;
        Task task = new Task();
        future = executor.submit(task);
    }

    @Override
    public double getValue() {
        return currentValue;
    }

    @Override
    public void stopMeasure() {
        future.cancel(true);
        executor.shutdownNow();
    }

    private class Task implements Callable<Void> {
        @Override
        public Void call() {
            // запускаем цикл генерации случайного значения скорости
            while (true) {
                currentValue = sleepAndGenerateRandomValue((int) (1000 / valueGenerationFrequency),
                    ACCELERATION_MIN, ACCELERATION_MAX);
                if (Thread.currentThread().isInterrupted()) {
                    break;  // выходим из бесконечного цикла
                }
            }
            return null;
        }
    }

}