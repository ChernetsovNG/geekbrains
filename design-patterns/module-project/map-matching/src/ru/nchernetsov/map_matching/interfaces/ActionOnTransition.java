package ru.nchernetsov.map_matching.interfaces;

import ru.nchernetsov.map_matching.EdgeInfo;

import java.util.List;

/**
 * Действие при переходе на другое ребро дорожного графа
 */
@FunctionalInterface
public interface ActionOnTransition {
    void execute(EdgeInfo edgeInfo, List<EdgeInfo> passedEdgesList);
}