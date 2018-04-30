package ru.nchernetsov.map_matching.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация алгоритма Map Matching'а
 */
public class MMAlgorithmConfig extends HashMap<String, Object> {
    // Cтроковые константы для доступа к элементам конфигурации
    private static final String BUFFER_SIZE = "bufferSize";
    public static final String MAX_METRIC_DEGRADATION_COUNT = "maxMetricDegradationCount";
    public static final String ZERO_SPEED_LIMIT = "zeroSpeedLimit";
    public static final String NORMAL_SPEED_SEARCH_BOX_SIZE = "normalSpeedSearchBoxSize";
    public static final String ZERO_SPEED_SEARCH_BOX_SIZE = "zeroSpeedSearchBoxSize";
    // Параметры поиска "по непрерывности"
    public static final String SEARCH_BY_CONTINUITY_RADIUS_FACTOR = "searchByContinuityRadiusFactor";
    // если следующая точка отстоит от предыдущей на время, большее чем эта уставка, то поиск "по непрерывности" не производится
    public static final String DELTA_TAU_CONTINUITY_LIMIT_MS = "deltaTauContinuityLimitMs";
    public static final String SEARCH_BY_CONTINUITY_PATH_LIMIT_FACTOR = "searchByContinuityPathLimitFactor";  // максимальная длина пути "по непрерывности"
    public static final String MAX_RECURSIVE_CALL_COUNT = "maxRecursiveCallCount";  // максимальное количество рекурсивных вызовов при поиске в исходящих рёбрах

    // параметры для расчёта метрики (расстояния Фреше):
    // 1. параметры, влияющие на значимость составляющих. Для алгоритма важны значения этих параметров относительно друг друга,
    // это определяет, какой из факторов будет иметь больший вес при сравнении (muAlpha - курс, muD - расстояние)
    public static final String muAlpha = "muAlpha";
    public static final String muD = "muD";
    // 2. параметры, влияющие на чувствительность к изменению факторов
    public static final String nAlpha = "nAlpha";
    public static final String nD = "nD";
    public static final String a = "a";

    public MMAlgorithmConfig(Map<String, Object> map) {
        super(map);
    }

    MMAlgorithmConfig(long bufferSize, long maxMetricDegradationCount, long maxRecursiveCallCount, double zeroSpeedLimit, double normalSpeedSearchBoxSize, double zeroSpeedSearchBoxSize,
                      double searchByContinuityRadiusFactor, long deltaTauContinuityLimitMs, double searchByContinuityPathLimitFactor,
                      double muAlpha, double muD, double nAlpha, double nD, double a) {
        this.put(BUFFER_SIZE, bufferSize);

        this.put(MAX_METRIC_DEGRADATION_COUNT, maxMetricDegradationCount);
        this.put(MAX_RECURSIVE_CALL_COUNT, maxRecursiveCallCount);
        this.put(ZERO_SPEED_LIMIT, zeroSpeedLimit);
        this.put(NORMAL_SPEED_SEARCH_BOX_SIZE, normalSpeedSearchBoxSize);
        this.put(ZERO_SPEED_SEARCH_BOX_SIZE, zeroSpeedSearchBoxSize);

        this.put(SEARCH_BY_CONTINUITY_RADIUS_FACTOR, searchByContinuityRadiusFactor);
        this.put(DELTA_TAU_CONTINUITY_LIMIT_MS, deltaTauContinuityLimitMs);
        this.put(SEARCH_BY_CONTINUITY_PATH_LIMIT_FACTOR, searchByContinuityPathLimitFactor);

        this.put(MMAlgorithmConfig.muAlpha, muAlpha);
        this.put(MMAlgorithmConfig.muD, muD);
        this.put(MMAlgorithmConfig.nAlpha, nAlpha);
        this.put(MMAlgorithmConfig.nD, nD);
        this.put(MMAlgorithmConfig.a, a);
    }
}
