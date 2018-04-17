package ru.nchernetsov;

import ru.nchernetsov.device.DeviceState;

import static ru.nchernetsov.message.ObjectKind.VEHICLE;

public class Main {
    public static void main(String[] args) {
        DeviceState deviceState = new DeviceState(100, VEHICLE);
        System.out.println(deviceState);
    }
}
