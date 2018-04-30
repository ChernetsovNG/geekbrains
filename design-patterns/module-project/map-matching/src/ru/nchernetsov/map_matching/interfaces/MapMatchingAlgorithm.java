package ru.nchernetsov.map_matching.interfaces;

import ru.nchernetsov.geometry.PosGPS;
import ru.nchernetsov.map_matching.MMResult;

import java.util.Optional;

/**
 * Алгоритм выполнения привязки точки к дорожному графу (Map Matching'а)
 */
public interface MapMatchingAlgorithm {
    Optional<MMResult> applyMM(long deviceID, PosGPS gps, DevicesMMResultsHolder devicesMMResultsHolder, ActionOnTransition onTransition, ActionOnShift onShift);
}
