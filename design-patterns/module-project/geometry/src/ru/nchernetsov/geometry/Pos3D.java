package ru.nchernetsov.geometry;

/**
 * расширение Vec3D, включающее в себя время UTC, трёхмерный курс и скорость
 */
public class Pos3D extends Point3D {
    private long timestamp;      // UNIX-time (с 00:00:00 UTC) 01.01.1970
    private Vec3D courseVector;  // вектор курса
    private double speed;        // в м/с

    private Pos3D(double x, double y, double z) {
        super(x,y,z);
        timestamp = 0;
        speed = 0.0;
        courseVector = new Vec3D(0.0, 0.0, 0.0);
    }

    Pos3D(Point3D point3D) {
        this(point3D.getX(), point3D.getY(), point3D.getZ());
    }

    public static Pos3D fromPosGPS(FromGeoTo3DTransformer transformer, PosGPS gps) {
        return transformer.posGPStoPos3D(gps);
    }

    public Point3D getPoint() {
        return new Point3D(getX(),getY(),getZ());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Vec3D getCourseVector() {
        return courseVector;
    }

    public void setCourseVector(Vec3D courseVector) {
        this.courseVector = courseVector;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
