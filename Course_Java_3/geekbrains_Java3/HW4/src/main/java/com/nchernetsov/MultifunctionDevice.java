package com.nchernetsov;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.concurrent.TimeUnit;

public class MultifunctionDevice {
    private final Object print = new Object();
    private final Object scan = new Object();

    public void printing() {
        synchronized (print) {
            int i = 1;
            while (i <= 10) {
                System.out.println("Printed " + i + " pages");
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }

    public void scanning() {
        synchronized (scan) {
            int i = 1;
            while (i <= 10) {
                System.out.println("Scanning " + i + " pages");
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }
}
