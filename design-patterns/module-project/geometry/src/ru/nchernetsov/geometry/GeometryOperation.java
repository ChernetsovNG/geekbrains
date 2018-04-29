package ru.nchernetsov.geometry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Функции для выполнения геометрических преобразований
 */
public class GeometryOperation {

    public static Point3D toPoint(Point3D fromPoint, Vec3D vec) {
        double toX = fromPoint.getX() + vec.getX();
        double toY = fromPoint.getY() + vec.getY();
        double toZ = fromPoint.getZ() + vec.getZ();

        return new Point3D(toX, toY, toZ);
    }

    // Скалярное произведение векторов: vec1 * vec2
    public static double scalarProduct(Vec3D vec1, Vec3D vec2) {
        return (vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY() + vec1.getZ() * vec2.getZ());
    }

    // Разность векторов: vec1 - vec2
    public static Vec3D vectorSubtract(Vec3D vec1, Vec3D vec2) {
        return new Vec3D(
            vec1.getX() - vec2.getX(),
            vec1.getY() - vec2.getY(),
            vec1.getZ() - vec2.getZ()
        );
    }

    public static double distanceBetweenTwo3Dpoints(Point3D point1, Point3D point2) {
        if (point1.equals(point2)) {
            return 0.0;
        }
        Vec3D vecFromPreviousToCurrent = new Vec3D(point1, point2);
        return vecFromPreviousToCurrent.length();
    }

    private static Point3D convertPosGeoToPoint3D(PosGEO posGEO) {
        return new FromGeoTo3DTransformer(posGEO.getLat(), posGEO.getLon()).point3DfromPosGEO(posGEO);
    }

    public static List<PointGeo> convertPosGEOListToPointGeoList(List<PosGEO> posGEOList) {
        return posGEOList.stream()
            .map(posGEO -> new PointGeo(posGEO.getLon(), posGEO.getLat())).collect(Collectors.toList());
    }

    public static List<Point3D> convertPosGEOListToPoint3DList(List<PosGEO> posGEOList) {
        return posGEOList.stream()
            .map(GeometryOperation::convertPosGeoToPoint3D).collect(Collectors.toList());
    }

}

