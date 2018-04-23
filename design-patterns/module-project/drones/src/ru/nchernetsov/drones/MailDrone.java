package ru.nchernetsov.drones;

import ru.nchernetsov.geo.GeoPoint;

import java.util.UUID;

/**
 * Почтовый дрон
 */
public class MailDrone implements Drone {
    private final UUID id;

    public MailDrone() {
        id = UUID.randomUUID();
    }

    @Override
    public void move(GeoPoint startPoint, GeoPoint endPoint) {
        System.out.printf("MailDrone: %s. Mail delivery from point: %s to point: %s",
            id, startPoint, endPoint);
        System.out.println();
    }

    @Override
    public void hold(GeoPoint position) {
        System.out.printf("MailDrone: %s. Hold on position: %s", id, position);
        System.out.println();
    }
}
