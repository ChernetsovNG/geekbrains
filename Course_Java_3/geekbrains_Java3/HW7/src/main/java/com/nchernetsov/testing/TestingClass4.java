package com.nchernetsov.testing;

import com.nchernetsov.annotation.AfterSuite;
import com.nchernetsov.annotation.BeforeSuite;
import com.nchernetsov.annotation.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nchernetsov.assertion.Assert.*;

// Тестируемый класс
public class TestingClass4 {
    List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    @Test
    public void test1() {
        list.add(10);
        assertEquals(6, list.size());
    }

    @Test(priority = 1)
    public void testLowPriority() {
        assertTrue(100 > 10);
    }

    @Test(priority = 10)
    public void testHighPriority() {
        assertNotNull(list);
    }

    @Test(priority = -1000)
    public void testWrongPriority() {
        System.out.println("This test doesn't work out");
    }
}
