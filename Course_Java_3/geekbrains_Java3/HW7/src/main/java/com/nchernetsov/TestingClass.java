package com.nchernetsov;

import com.nchernetsov.annotation.AfterSuite;
import com.nchernetsov.annotation.BeforeSuite;
import com.nchernetsov.annotation.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nchernetsov.assertion.Assert.assertEquals;
import static com.nchernetsov.assertion.Assert.assertNotNull;
import static com.nchernetsov.assertion.Assert.assertTrue;

// Тестируемый класс
public class TestingClass {
    List<Integer> list = new ArrayList<>();

    @BeforeSuite
    public void beforeTests() {
        System.out.println("Before suite");
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(list);
        System.out.println("");
    }

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

    @AfterSuite
    public void afterTests() {
        System.out.println("After suite");
        list.clear();
        System.out.println(list);
    }
}
