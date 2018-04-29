package ru.nchernetsov.map_matching;

import ru.nchernetsov.geometry.Pos3D;
import ru.nchernetsov.geometry.Vec3D;

import java.util.function.BiFunction;

import static java.lang.StrictMath.abs;
import static ru.nchernetsov.geometry.GeometryOperation.scalarProduct;
import static ru.nchernetsov.geometry.GeometryOperation.vectorSubtract;

/**
 * Проекция точки на сегмент ребра дорожного графа
 */
class ProjOnSegment {
    final double length;                         // Длина сегмента, на который проецируется точка
    double vecFromBegSegmentToPosProjOnSegment;  // Длина проекции вектора из начала сегмента в рассматриваемую точку на сегмент
    final double distance;                       // Расстояние от точки до сегмента
    double correlation;                          // Длина проекция вектора курса в Pos3D и направляющего вектора сегмента
    final double metric;

    ProjOnSegment(RouteSegment segment, Pos3D pos, ProjOnEdge.Direction direction, BiFunction<Double, Double, Double> calcMetricFunction) {
        length = segment.getLength();
        distance = calcDistanceFromPosToSegment(segment, pos);
        metric = calcProjMetric(segment, pos, direction, calcMetricFunction);
    }

    private double calcDistanceFromPosToSegment(RouteSegment segment, Pos3D pos) {
        Vec3D vecFromBegSegmentToPos = new Vec3D(segment.getFromPoint(), pos.getPoint());
        vecFromBegSegmentToPosProjOnSegment = scalarProduct(vecFromBegSegmentToPos, segment.getDirVector());

        if (vecFromBegSegmentToPosProjOnSegment < 0) {
            return vecFromBegSegmentToPos.length();
        } else if (vecFromBegSegmentToPosProjOnSegment > length) {
            Vec3D vecFromSegmentEndToPos = new Vec3D(segment.getToPoint(), pos.getPoint());
            return vecFromSegmentEndToPos.length();
        } else {
            //вектор длины, равной длине проекции, направленный вдоль сегмента
            Vec3D tprojVector = new Vec3D(segment.getDirVector());
            tprojVector.multiplyByNumber(vecFromBegSegmentToPosProjOnSegment);

            Vec3D normalVector = vectorSubtract(vecFromBegSegmentToPos, tprojVector);  //вектор из точки relPosOnEdge на сегмент (перпендикуляр)

            return normalVector.length();
        }
    }

    private double calcProjMetric(RouteSegment segment, Pos3D pos, ProjOnEdge.Direction direction, BiFunction<Double, Double, Double> calcMetricFunction) {
        double projCourseOnSegmentLength = scalarProduct(pos.getCourseVector(), segment.getDirVector());
        switch (direction) {
            case FORWARD:
                correlation = projCourseOnSegmentLength;
                return calcMetricFunction.apply(distance, correlation);
            case BACKWARD:
                correlation = -1.0 * projCourseOnSegmentLength;
                return calcMetricFunction.apply(distance, correlation);
            case BOTH:
                correlation = projCourseOnSegmentLength;
                return calcMetricFunction.apply(distance, abs(correlation));
        }
        return 0.0;
    }

    //точка слева от сегмента (или совпадает с его началом)
    boolean isPointOutLeft() {
        return vecFromBegSegmentToPosProjOnSegment <= 0;
    }

    //точка справа от сегмента (или совпадает с его концом)
    boolean isPointOutRight() {
        return vecFromBegSegmentToPosProjOnSegment >= length;
    }

    //точка внутри сегмента (не включая границы)
    boolean isPointInside() {
        return !(isPointOutLeft() || isPointOutRight());
    }

    @Override
    public String toString() {
        return "ProjOnSegment{" +
            "length=" + length +
            ", vecFromBegSegmentToPosProjOnSegment=" + vecFromBegSegmentToPosProjOnSegment +
            ", distance=" + distance +
            ", correlation=" + correlation +
            ", metric=" + metric +
            '}';
    }
}
