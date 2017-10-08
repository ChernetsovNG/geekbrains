package com.geek.spaceshooter.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameXander on 07.10.2017.
 */

public class Route {
    private List<Vector2> velocityList;
    private List<Integer> points;
    private Vector2 initialPosition;

    public Vector2 getInitialPosition() {
        return initialPosition;
    }

    public Route(Vector2 initialPosition) {
        this.initialPosition = initialPosition;
        this.velocityList = new ArrayList<Vector2>();
        this.points = new ArrayList<Integer>();
    }

    public Vector2 getMyVelocity(int coordX) {
        for (int i = points.size() - 1; i >= 0; i--) {
            if (coordX < points.get(i)) {
                return velocityList.get(i);
            }
        }
        throw new RuntimeException("Route Exception");
    }

    public Route addPoint(int nextPoint, Vector2 velocity) {
        points.add(nextPoint);
        velocityList.add(velocity);
        return this;
    }
}
