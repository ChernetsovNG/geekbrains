package ru.nchernetsov.device;

import ru.nchernetsov.geo.GeoPoint;
import ru.nchernetsov.message.ObjectKind;

import java.util.Objects;

/**
 * Класс, характеризующий состояние устройства в последней отправленной точке
 */
public class DeviceState {
    // Когда приходит точка для устройства, которого не было в кеше, используем это время
    private static final long NEW_DEVICE_UTC = 0;
    /**
     * Тип устройства
     */
    private ObjectKind objectKind;
    /**
     * Идентификатор устройства
     */
    private int deviceId;
    /**
     * Время в последней точке
     */
    private long utc = NEW_DEVICE_UTC;
    /**
     * Географические координаты
     */
    private GeoPoint point;
    /**
     * Скорость
     */
    private double spd;
    /**
     * Признак валидности координат
     */
    private boolean vld;
    /**
     * Статус связи устройства: true - на связи, false - не на связи
     */
    private boolean isConnect;
    /**
     * Создано ли это состояние только что? Или уже было раньше и взято из кеша?
     */
    private boolean isNewlyCreated;
    /**
     * Статус движения устройства
     */
    private MovingStatus movingStatus;

    public DeviceState(int deviceId, ObjectKind objectKind) {
        this.deviceId = deviceId;
        this.objectKind = objectKind;
    }

    public ObjectKind getObjectKind() {
        return objectKind;
    }

    public long getUtc() {
        return utc;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public double getSpd() {
        return spd;
    }

    public boolean isVld() {
        return vld;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }

    public MovingStatus getMovingStatus() {
        return movingStatus;
    }

    public int getDeviceId() {
        return deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceState that = (DeviceState) o;
        return utc == that.utc &&
            Double.compare(that.spd, spd) == 0 &&
            vld == that.vld &&
            isConnect == that.isConnect &&
            isNewlyCreated == that.isNewlyCreated &&
            deviceId == that.deviceId &&
            objectKind == that.objectKind &&
            Objects.equals(point, that.point) &&
            movingStatus == that.movingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectKind, utc, point, spd, vld, isConnect, isNewlyCreated, movingStatus, deviceId);
    }
}
