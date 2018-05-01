package ru.nchernetsov.map_matching.interfaces;

import ru.nchernetsov.map_matching.EdgeInfo;

/**
 * Действие при движении (смещении) по ребру дорожного графа
 */
@FunctionalInterface
public interface ActionOnShift {
    void execute(EdgeInfo edgeInfo);
}