package com.nchernetsov;

import static com.nchernetsov.MainClass.semaphore;

public class Tunnel extends Stage {
    public Tunnel(int length) {
        this.length = length;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car car) {
        try {
            try {
                semaphore.acquire();
                System.out.println(car.getName() + " начал этап: " + description);
                Thread.sleep((length / car.getSpeed()) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(car.getName() + " закончил этап: " + description);
                semaphore.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
