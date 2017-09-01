package com.nchernetsov;

import org.junit.Test;

import static com.nchernetsov.Main.method1;
import static org.junit.Assert.*;

public class MainMethod1Test {
    @Test
    public void method1NormalTest() {
        int[] array = new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7};
        int[] result = method1(array);
        assertArrayEquals(new int[] {1, 7}, result);
    }

    @Test(expected = RuntimeException.class)
    public void method1No4Test() {
        int[] array = new int[]{1, 2, 3};
        int[] result = method1(array);
    }

    @Test
    public void method1Last4Test() {
        int[] array = new int[]{1, 2, 3, 3, 4};
        int[] result = method1(array);
        assertArrayEquals(new int[] {}, result);
    }

    @Test
    public void method1First4Test() {
        int[] array = new int[]{4, 1, 2, 3, 5};
        int[] result = method1(array);
        assertArrayEquals(new int[] {1, 2, 3, 5}, result);
    }

}