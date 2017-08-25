package com.nchernetsov;

public class Main {
    private final Object sync = new Object();
    private final int nTimesToWrite = 5;
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        // 1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз,
        // порядок должен​ ​ быть​ ​ именно​ ​ ABСABСABС
        final Main mainObject = new Main();

        Thread threadA = new Thread(() -> mainObject.printLetter('A', 'B'));
        Thread threadB = new Thread(() -> mainObject.printLetter('B', 'C'));
        Thread threadC = new Thread(() -> mainObject.printLetter('C', 'A'));

        threadA.start();
        threadB.start();
        threadC.start();
    }

    public void printLetter(char letterToPrint, char nextLetter) {
        synchronized (sync) {
            try {
                for (int i = 0; i < nTimesToWrite; i++) {
                    while (currentLetter != letterToPrint) {
                        sync.wait();
                    }
                    System.out.print(letterToPrint);
                    currentLetter = nextLetter;
                    sync.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
