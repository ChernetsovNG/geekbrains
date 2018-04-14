package ru.ncherenetsov.device;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для операций с DeviceState'ами. Singleton
 */
public class DeviceService {
    private static volatile DeviceService instance;

    private final Map<Integer, DeviceState> cache = new HashMap<>();

    private DeviceService() {
    }

    public static DeviceService getInstance() {
        if (instance == null) {
            synchronized (DeviceService.class) {
                if (instance == null) {
                    instance = new DeviceService();
                }
            }
        }
        return instance;
    }

    public void addDeviceState(DeviceState deviceState) {
        cache.putIfAbsent(deviceState.getDeviceId(), deviceState);
    }

    public void removeDeviceState(DeviceState deviceState) {
        cache.remove(deviceState.getDeviceId());
    }

    public Collection<DeviceState> getAllDeviceStates() {
        return cache.values();
    }

    public DeviceState getDeviceState(int deviceId) {
        return cache.get(deviceId);
    }
}
