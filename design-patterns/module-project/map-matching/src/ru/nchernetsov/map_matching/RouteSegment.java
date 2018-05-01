package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.GeometryOperation;
import ru.nchernetsov.geometry.Point3D;
import ru.nchernetsov.geometry.Vec3D;

/**
 * сегмент (отрезок прямой) ребра дорожного графа
 */
public class RouteSegment {
    private final Point3D fromPoint;  // из точки
    private final Vec3D dirVector;    // направляющий вектор
    private final double length;      // длина сегмента
    private final Point3D toPoint;    // в точку

    public RouteSegment(Point3D fromPoint, Vec3D dirVector, double length) {
        this.fromPoint = fromPoint;
        this.dirVector = dirVector;
        this.length = length;

        Vec3D dirMultOnLen = new Vec3D(dirVector);
        dirMultOnLen.multiplyByNumber(length);
        toPoint = GeometryOperation.toPoint(fromPoint, dirMultOnLen);
    }

    public Point3D getFromPoint() {
        return fromPoint;
    }

    public Vec3D getDirVector() {
        return dirVector;
    }

    public double getLength() {
        return length;
    }

    public Point3D getToPoint() {
        return toPoint;
    }

    @Override
    public String toString() {
        return "RouteSegment{" +
            "fromPoint=" + fromPoint +
            ", dirVector=" + dirVector +
            ", length=" + length +
            ", toPoint=" + toPoint +
            '}';
    }
}
