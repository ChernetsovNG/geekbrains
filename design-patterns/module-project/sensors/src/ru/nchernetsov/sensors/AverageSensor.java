package ru.nchernetsov.sensors;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Датчик, возвращающий значение N последних измерений
 */
public class AverageSensor extends AbstractSensor {
    private final Queue<Double> measures = new LinkedList<>();
    private final int N;

    public AverageSensor(SensorImpl implementor, int N) {
        super(implementor);
        this.N = N;
    }

    public double getAverageValue() {
        measures.add(implementor.getValue());
        if (measures.size() > N) {
            measures.remove();
        }
        return measures.stream().mapToDouble(elem -> elem).average().orElse(0.0);
    }
}
