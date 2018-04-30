package ru.nchernetsov.geometry;

import static java.lang.Math.cos;
import static java.lang.StrictMath.*;
import static ru.nchernetsov.geometry.WGS84.*;

/**
 * Класс для преобразования из геокоординат в 3D-пространство, связанное с центром масс Земли
 */
public class FromGeoTo3DTransformer {
    private final double sinLat;  // синус широты
    private final double cosLat;  // косинус долготы
    private final double sinLon;  // синус долготы
    private final double cosLon;  // косинус долготы
    private final double N;       //

    public FromGeoTo3DTransformer(double lat, double lon) {
        double latRad = toRadians(lat);
        double lonRad = toRadians(lon);
        sinLat = sin(latRad);
        cosLat = cos(latRad);
        sinLon = sin(lonRad);
        cosLon = cos(lonRad);
        N = A/sqrt(1.0 - E2*sinLat*sinLat);
    }

    // Преобразование GPS-координаты в вектор Pos3D
    // в разделах 2.4, 2.5 документа https://microem.ru/files/2012/08/GPS.G1-X-00006.pdf
    // описано преобразование скоростей из 3D координат в локальную касательную плоскость (local tangent plane). Наше преобразование - обратное
    public Pos3D posGPStoPos3D(PosGPS posGPS) {
        Pos3D pos3D = new Pos3D(point3DfromPosGEO(posGPS));  // устанавливаем в pos3D координаты x,y,z

        pos3D.setTimestamp(posGPS.getTimestamp());  // устанавливаем в pos3D временную отметку

        double x = pos3D.getX();
        double y = pos3D.getY();
        double z = pos3D.getZ();

        double proj_xy = (N + posGPS.getAltitude())*cosLat;  // длина проекции радиус-вектора на плоскость X-Y

        double tan_lat = z/proj_xy;                             // приближённо - тангенс широты (но ещё видимо учитывается отклонение эллипса от шара) tg(fi)
        double cos_lat = 1.0/sqrt(tan_lat*tan_lat*A_B4 + 1.0);  // приближённо - косинус широты cos(fi)
        double sin_lat = tan_lat*cos_lat*A_B2;                  // приближённо - tg(fi)*cos(fi) = sin(fi)

        double sin_lat_cos_lon = sin_lat*(x/proj_xy);  // приближённо - sin(fi)*cos(lam)
        double sin_lat_sin_lon = sin_lat*(y/proj_xy);  // приближённо - sin(fi)*cos(90-lam) = sin(fi)*sin(lam)

        double gpsCourse = posGPS.getAzimuth();          // азимут
        double courseEast = sin(toRadians(gpsCourse));   // проекция единичного вектора направления на ось X (на восток)
        double courseNorth = cos(toRadians(gpsCourse));  // проекция единичного вектора направления на ось Y (на север)

        double sin_lon;
        double cos_lon;

        if (abs(x) > abs(y)) {
            double tg_lon = y/x;
            double tg_lon_sqr = tg_lon*tg_lon;
            cos_lon = (x < 0.0) ? -1.0/sqrt(tg_lon_sqr + 1.0) : 1.0/sqrt(tg_lon_sqr + 1.0);
            sin_lon = tg_lon*cos_lon;
        } else {
            double ctg_lon = x/y;
            double ctg_lon_sqr = ctg_lon*ctg_lon;
            sin_lon = (y < 0.0) ? 1.0/sqrt(ctg_lon_sqr + 1.0) : -1.0/sqrt(ctg_lon_sqr + 1.0);
            cos_lon = ctg_lon*sin_lon;
        }

        //устанавливаем в pos3D вектор курса
        double cX = courseEast*(-1.0)*sin_lon + courseNorth*(-1.0)*sin_lat_cos_lon;
        double cY = courseEast*cos_lon + courseNorth*(-1.0)*sin_lat_sin_lon;
        double cZ = courseNorth*cos_lat;

        pos3D.setCourseVector(new Vec3D(cX,cY,cZ));

        pos3D.setSpeed(posGPS.getSpeed());  //устанавливаем в pos3D скорость

        return pos3D;
    }

    //Преобразование геокоординат в декартовы координаты: формулы - в соответствии с
    //https://en.wikipedia.org/wiki/Geographic_coordinate_conversion#From_geodetic_to_ECEF_coordinates
    public Point3D point3DfromPosGEO(PosGEO posGEO) {
        double geoAltitude = posGEO.getAltitude();

        double x = (N + geoAltitude)*cosLat*cosLon;
        double y = (N + geoAltitude)*cosLat*sinLon;
        double z = (WGS84.OneMinusE2*N + geoAltitude)*sinLat;

        return new Point3D(x, y, z);
    }

    public Point3D point3DfromPointGEO(PointGeo pointGeo) {
        double x = N*cosLat*cosLon;
        double y = N*cosLat*sinLon;
        double z = WGS84.OneMinusE2*N*sinLat;

        return new Point3D(x, y, z);
    }

    public double[] makeBoxHalfSizeInDegrees(double meters) {
        double radius = N*cosLat;
        double perimeter = 2*PI*radius;
        double metersPerDegree = perimeter/360.0;

        return new double[] {
            meters/metersPerDegree,
            meters/(1000.0*KMperDegM)
        };
    }
}
