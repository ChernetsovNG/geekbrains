package com.nchernetsov;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MainClass {
    public static final int CARS_COUNT = 4;

    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT,
        () -> System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!"));      // для моделирования одновременного старта

    public static Semaphore semaphore = new Semaphore(CARS_COUNT / 2);      // для моделирования узких мест (тоннелей)

    public static CountDownLatch countDownLatch = new CountDownLatch(CARS_COUNT);  // для определения окончания гонки

    public static Semaphore winnerSemaphore = new Semaphore(0);  // для определения победителя
    public static String winner;

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(80), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (Car car : cars) {
            new Thread(car).start();
        }

        try {
            winnerSemaphore.acquire();
            System.out.println(winner + " WIN the race!!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась");
    }
}
