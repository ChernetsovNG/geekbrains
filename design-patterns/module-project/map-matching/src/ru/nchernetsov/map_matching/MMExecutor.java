package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.PosGPS;
import ru.nchernetsov.map_matching.algorithm.AlgorithmName;
import ru.nchernetsov.map_matching.algorithm.IncrementalMMAlgorithm;
import ru.nchernetsov.map_matching.algorithm.MMAlgorithmFindPath;
import ru.nchernetsov.map_matching.cache.CacheFilesHandler;
import ru.nchernetsov.map_matching.interfaces.ActionOnShift;
import ru.nchernetsov.map_matching.interfaces.ActionOnTransition;
import ru.nchernetsov.map_matching.interfaces.DevicesMMResultsHolder;
import ru.nchernetsov.map_matching.interfaces.MapMatchingAlgorithm;

import java.util.Map;
import java.util.Optional;

/**
 * Класс для выполнения Map Matching'а (реализация алгоритма "Стратегия")
 */
public class MMExecutor implements MapMatchingAlgorithm {
    private MapMatchingAlgorithm algorithm;

    public MMExecutor(CacheFilesHandler cacheFilesHandler, AlgorithmName algorithmName, Map<String, Object> algorithmConfig) {
        switch (algorithmName) {
            case IncrementalMMAlgorithm:
                this.algorithm = new IncrementalMMAlgorithm(cacheFilesHandler, algorithmConfig);
                break;
            case MMAlgorithmFindPath:
                this.algorithm = new MMAlgorithmFindPath(cacheFilesHandler, algorithmConfig);
                break;
        }
    }

    @Override
    public Optional<MMResult> applyMM(long deviceID, PosGPS gps, DevicesMMResultsHolder devicesMMResultsHolder, ActionOnTransition onTransition, ActionOnShift onShift) {
        return algorithm.applyMM(deviceID, gps, devicesMMResultsHolder, onTransition, onShift);
    }

    public void setAlgorithm(MapMatchingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
