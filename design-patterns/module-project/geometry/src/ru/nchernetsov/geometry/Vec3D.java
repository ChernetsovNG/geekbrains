package ru.nchernetsov.geometry;

/**
 * Трёхмерный вектор
 */
public class Vec3D {
    private double x;
    private double y;
    private double z;

    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(Vec3D other) {
        this(other.getX(), other.getY(), other.getZ());
    }

    public Vec3D(Point3D start, Point3D end) {
        this(
            end.getX() - start.getX(),
            end.getY() - start.getY(),
            end.getZ() - start.getZ()
        );
        if (start.equals(end)) {  // если start = end, то length = 0 и нормализация не сработает
            throw new RuntimeException("Attempt to create vector from coincident points");
        }
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    //нормализация вектора
    public void normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
    }

    public void multiplyByNumber(double number) {
        this.x *= number;
        this.y *= number;
        this.z *= number;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Vec3D{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            '}';
    }
}
