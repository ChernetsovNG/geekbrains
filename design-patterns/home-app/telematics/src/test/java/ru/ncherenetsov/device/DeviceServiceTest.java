package ru.ncherenetsov.device;

import org.junit.jupiter.api.Test;
import ru.ncherenetsov.message.ObjectKind;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeviceServiceTest {

    @Test
    void deviceServiceCacheTest() {
        DeviceState deviceState1 = new DeviceState(1001, ObjectKind.VEHICLE);
        DeviceState deviceState2 = new DeviceState(1002, ObjectKind.VEHICLE);

        DeviceService deviceService = DeviceService.getInstance();

        deviceService.addDeviceState(deviceState1);
        deviceService.addDeviceState(deviceState2);

        assertEquals(2, deviceService.getAllDeviceStates().size());
        assertEquals(deviceState1, deviceService.getDeviceState(1001));
        assertEquals(deviceState2, deviceService.getDeviceState(1002));
    }
}
