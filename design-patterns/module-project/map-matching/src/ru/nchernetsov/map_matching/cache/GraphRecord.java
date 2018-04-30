package ru.nchernetsov.map_matching.cache;

import java.util.List;

public class GraphRecord {
    private final List<Integer> forwardOutEdges;
    private final List<Integer> backwardOutEdges;

    public GraphRecord(List<Integer> forwardOutEdges, List<Integer> backwardOutEdges) {
        this.forwardOutEdges = forwardOutEdges;
        this.backwardOutEdges = backwardOutEdges;
    }

    public List<Integer> getForwardOutEdges() {
        return forwardOutEdges;
    }

    public List<Integer> getBackwardOutEdges() {
        return backwardOutEdges;
    }

    @Override
    public String toString() {
        return "GraphRecord{" +
            "forwardOutEdges=" + forwardOutEdges +
            ", backwardOutEdges=" + backwardOutEdges +
            '}';
    }
}
