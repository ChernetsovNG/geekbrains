package ru.nchernetsov.geo;

import java.util.Objects;

/**
 * Характеристики точки посылки сообщения
 */
public class TSPos {
    /**
     * Географические координаты (широта, долгота, высота над уровнем моря)
     */
    private GeoPoint point;
    /**
     * Время (UNIX-time), вычисленное по спутникам (время навигации)
     */
    private long ntm;
    /**
     * Скорость, м/с
     */
    private double spd;
    /**
     * Азимут, град. отсчитанные от направления на север по часовой стрелке
     */
    private double dir;
    /**
     * Признак валидности координат
     */
    private boolean vld;

    private TSPos() {
    }

    public GeoPoint getPoint() {
        return point;
    }

    public long getNtm() {
        return ntm;
    }

    public double getSpd() {
        return spd;
    }

    public double getDir() {
        return dir;
    }

    public boolean isVld() {
        return vld;
    }

    public static Builder newBuilder() {
        return new TSPos().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setPoint(GeoPoint point) {
            TSPos.this.point = point;
            return this;
        }

        public Builder setNtm(long ntm) {
            TSPos.this.ntm = ntm;
            return this;
        }

        public Builder setSpd(double spd) {
            TSPos.this.spd = spd;
            return this;
        }

        public Builder setDir(double dir) {
            TSPos.this.dir = dir;
            return this;
        }

        public Builder setVld(boolean vld) {
            TSPos.this.vld = vld;
            return this;
        }

        public TSPos build() {
            return TSPos.this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TSPos tsPos = (TSPos) o;
        return ntm == tsPos.ntm &&
            Double.compare(tsPos.spd, spd) == 0 &&
            Double.compare(tsPos.dir, dir) == 0 &&
            vld == tsPos.vld &&
            Objects.equals(point, tsPos.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, ntm, spd, dir, vld);
    }
}
