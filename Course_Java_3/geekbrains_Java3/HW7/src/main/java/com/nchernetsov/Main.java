package com.nchernetsov;

import com.nchernetsov.testing.TestingClass1;
import com.nchernetsov.testing.TestingClass2;
import com.nchernetsov.testing.TestingClass3;
import com.nchernetsov.testing.TestingClass4;

public class Main {
    public static void main(String[] args) {
        Tester.start(TestingClass1.class);

        try {
            System.out.println("---------------------------");
            Tester.start(TestingClass2.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("---------------------------");
            Tester.start(TestingClass3.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("---------------------------");
        Tester.start(TestingClass4.class);
    }
}
