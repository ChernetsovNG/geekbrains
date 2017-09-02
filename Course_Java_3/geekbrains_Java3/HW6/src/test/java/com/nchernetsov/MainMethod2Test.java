package com.nchernetsov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.nchernetsov.Main.method2;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainMethod2Test {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {new int[] {1, 4, 1, 4}, true},
            {new int[] {1, 1, 1, 1}, false},
            {new int[] {4, 4, 4, 4}, false},
            {new int[] {1, 4, 1, 4, 3}, false},
            {new int[] {}, false},
            {new int[] {2, 2, 2, 2}, false}
        });
    }

    private final int[] array;
    private final boolean testResult;

    public MainMethod2Test(int[] array, boolean testResult) {
        this.array = array;
        this.testResult = testResult;
    }

    @Test
    public void method2Test() {
        assertEquals(testResult, method2(array));
    }
}
