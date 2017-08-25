package com.nchernetsov;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    private final Object sync = new Object();
    private final int nTimesToWrite = 5;
    private volatile char currentLetter = 'A';

    private final RandomString randomString = new RandomString(6);

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

        try {
            threadA.join();
            threadB.join();
            threadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. Написать метод, в котором 3 потока построчно пишут данные в файл
        // (по​ ​10​ ​записей,​ ​с ​периодом​ в ​20​ ​мс)
        try {
            FileOutputStream fos = new FileOutputStream("test_file.txt");

            Thread writer1 = new Thread(() -> mainObject.printDataIntoFile(fos, 10));
            Thread writer2 = new Thread(() -> mainObject.printDataIntoFile(fos, 10));
            Thread writer3 = new Thread(() -> mainObject.printDataIntoFile(fos, 10));

            writer1.start();
            writer2.start();
            writer3.start();

            writer1.join();
            writer2.join();
            writer3.join();

            fos.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
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

    public void printDataIntoFile(FileOutputStream fileOutputStream, int countStringsToWrite) {
        for (int i = 0; i < countStringsToWrite; i++) {
            try {
                String string = randomString.nextString();
                fileOutputStream.write(string.getBytes());
                fileOutputStream.write("|".getBytes());  // пишем разделитель
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
