package com.nchernetsov;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.nchernetsov.Main.convertArrayToList;
import static com.nchernetsov.Main.printArray;
import static com.nchernetsov.Main.swapArrayElements;
import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void swapArrayElementsTest() {
        Integer[] array = new Integer[]{1, 2, 3, 4, 5};

        swapArrayElements(array, 0, 4);

        assertEquals(new Integer(5), array[0]);
        assertEquals(new Integer(1), array[4]);
    }

    @Test
    public void convertArrayToListTest() {
        Float[] array = new Float[]{1f, 2f, 3f, 4f, 5f};

        ArrayList<Float> arrayList = convertArrayToList(array);

        assertEquals(Arrays.asList(array), arrayList);
    }

}