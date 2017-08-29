package com.nchernetsov;

import static com.nchernetsov.MainClass.*;

public class Car implements Runnable {
    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        System.out.println(this.name + " завершил прохождение маршрута");
        countDownLatch.countDown();

        if (winner == null) {
            winner = this.name;
            winnerSemaphore.release();
        }
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }
}
