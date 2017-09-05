package com.nchernetsov.testing;

import com.nchernetsov.annotation.AfterSuite;
import com.nchernetsov.annotation.Test;

public class TestingClass3 {
    @Test
    public void test1() {}

    @AfterSuite
    public void beforeSuite1() {
    }

    @AfterSuite
    public void beforeSuite2() {
    }
}
