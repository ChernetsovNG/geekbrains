package com.nchernetsov.assertion;

public class Assert {
    public static void assertEquals(Object expected, Object actual) {
        if (equalsRegardingNull(expected, actual)) {
            return;
        } else {
            throw new TestException("Objects are not equals");
        }
    }

    public static void assertNotNull(Object object) {
        assertTrue(object != null);
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            fail();
        }
    }

    public static void fail() {
        throw new TestException();
    }

    public static void fail(String message) {
        throw new TestException(message);
    }

    private static boolean equalsRegardingNull(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }
        return isEquals(expected, actual);
    }

    private static boolean isEquals(Object expected, Object actual) {
        return expected.equals(actual);
    }
}
