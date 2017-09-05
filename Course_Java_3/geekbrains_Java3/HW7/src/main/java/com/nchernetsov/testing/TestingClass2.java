package com.nchernetsov.testing;

import com.nchernetsov.annotation.BeforeSuite;
import com.nchernetsov.annotation.Test;

public class TestingClass2 {
    @BeforeSuite
    public void beforeSuite1() {
    }

    @BeforeSuite
    public void beforeSuite2() {
    }

    @Test
    public void test1() {}

}
