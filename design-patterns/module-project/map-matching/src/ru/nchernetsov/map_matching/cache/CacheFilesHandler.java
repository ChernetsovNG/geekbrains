package ru.nchernetsov.map_matching.cache;

import ru.nchernetsov.geometry.Point3D;

import java.util.List;

/**
 * Класс для извлечения информации из файлов дорожного графа
 */
public interface CacheFilesHandler {
    List<Integer> rTreeSearch(double minX, double minY, double maxX, double maxY);

    int getGeomId(int routeEdgeID);

    GeometryEdgeMappingRecord getGeometryEdgeMappingRecord(int geomID);

    GeometryRecord getGeometryRecordByGeometryPos(long geometryPos);

    GraphRecord getGraphRecord(int forwardId, int backwardId);

    List<Integer> getOutEdgesByEdgeId(double pathLastEdge);

    double getLengthByEdgeId(double pathLastEdge);

    Point3D getGeometryMiddlePointByGeomId(int currentEdgeGeomId);
}
