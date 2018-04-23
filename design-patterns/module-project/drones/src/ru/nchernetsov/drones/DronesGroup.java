package ru.nchernetsov.drones;

import ru.nchernetsov.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Группа дронов
 */
public class DronesGroup implements Drone, Attacking {
    private final UUID id;

    private final List<Drone> drones = new ArrayList<>();

    public DronesGroup() {
        id = UUID.randomUUID();
    }

    public void addDrone(Drone drone) {
        drones.add(drone);
    }

    public void remove(Drone drone) {
        drones.remove(drone);
    }

    @Override
    public void move(GeoPoint startPoint, GeoPoint endPoint) {
        System.out.printf("DronesGroup: %s. Move from point: %s to point: %s =>", id, startPoint, endPoint);
        System.out.println();
        drones.forEach(drone -> drone.move(startPoint, endPoint));
    }

    @Override
    public void hold(GeoPoint position) {
        System.out.printf("DronesGroup: %s. Hold on position: %s =>", id, position);
        System.out.println();
        drones.forEach(drone -> drone.hold(position));
    }

    @Override
    public void attack() {
        System.out.printf("DronesGroup: %s. Attacking! =>", id);
        System.out.println();
        drones.forEach(drone -> {
            if (drone instanceof Attacking) {
                ((Attacking) drone).attack();
            }
        });
    }
}
