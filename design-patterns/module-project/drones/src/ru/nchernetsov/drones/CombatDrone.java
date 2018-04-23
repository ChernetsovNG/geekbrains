package ru.nchernetsov.drones;

import ru.nchernetsov.geo.GeoPoint;

import java.util.UUID;

public class CombatDrone implements Drone, Attacking {
    private final UUID id;

    public CombatDrone() {
        id = UUID.randomUUID();
    }

    @Override
    public void move(GeoPoint startPoint, GeoPoint endPoint) {
        System.out.printf("CombatDrone: %s. Move from point: %s to point: %s",
            id, startPoint, endPoint);
        System.out.println();
    }

    @Override
    public void hold(GeoPoint position) {
        System.out.printf("CombatDrone: %s. Hold on position: %s", id, position);
        System.out.println();
    }

    @Override
    public void attack() {
        System.out.printf("CombatDrone: %s. Attacking!", id);
        System.out.println();
    }
}
