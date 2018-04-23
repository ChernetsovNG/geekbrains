package ru.nchernetsov.drones.test;

import ru.nchernetsov.drones.CombatDrone;
import ru.nchernetsov.drones.Drone;
import ru.nchernetsov.drones.DronesGroup;
import ru.nchernetsov.drones.MailDrone;
import ru.nchernetsov.geo.GeoPoint;

public class DronesTest {
    public static void main(String[] args) {
        Drone mailDrone1 = new MailDrone();
        Drone mailDrone2 = new MailDrone();
        Drone combatDrone1 = new CombatDrone();

        mailDrone1.move(new GeoPoint(30.0, 30.0), new GeoPoint(31.0, 30.5));
        mailDrone2.hold(new GeoPoint(51.1, 52.2));
        ((CombatDrone) combatDrone1).attack();

        System.out.println();

        DronesGroup dronesGroup = new DronesGroup();

        dronesGroup.addDrone(mailDrone1);
        dronesGroup.addDrone(mailDrone2);
        dronesGroup.addDrone(combatDrone1);

        dronesGroup.move(new GeoPoint(30.0, 30.0), new GeoPoint(31.0, 30.5));
        dronesGroup.hold(new GeoPoint(31.0, 30.5));
        dronesGroup.attack();
    }

}
