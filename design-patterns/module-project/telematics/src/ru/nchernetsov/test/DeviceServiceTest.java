package ru.nchernetsov.test;

import ru.nchernetsov.device.DeviceService;
import ru.nchernetsov.device.DeviceState;
import ru.nchernetsov.message.ObjectKind;

import static org.junit.Assert.assertEquals;
import static ru.nchernetsov.message.ObjectKind.VEHICLE;

public class DeviceServiceTest {

    public static void main(String[] args) {
        DeviceServiceTest deviceServiceTest = new DeviceServiceTest();
        deviceServiceTest.deviceServiceCacheTest();
    }

    void deviceServiceCacheTest() {
        DeviceState deviceState1 = new DeviceState(1001, VEHICLE);
        DeviceState deviceState2 = new DeviceState(1002, VEHICLE);

        DeviceService deviceService = DeviceService.getInstance();

        deviceService.addDeviceState(deviceState1);
        deviceService.addDeviceState(deviceState2);

        assertEquals(2, deviceService.getAllDeviceStates().size());
        assertEquals(deviceState1, deviceService.getDeviceState(1001));
        assertEquals(deviceState2, deviceService.getDeviceState(1002));
    }
}
