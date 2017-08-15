package com.nchernetsov;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoxTest {
    private static final Box<Apple> appleBox1 = createAppleBox(4);
    private static final Box<Apple> appleBox2 = createAppleBox(6);
    private static final Box<Orange> orangeBox = createOrangeBox(4);

    @Test
    public void addFruitTest() {
        assertEquals(4.0f, appleBox1.getWeight(), 1e-3);
        appleBox1.addFruit(new Apple());
        assertEquals(5.0f, appleBox1.getWeight(), 1e-3);
    }

    @Test
    public void getWeightTest() {
        assertEquals(6.0f, orangeBox.getWeight(), 1e-3);
    }

    @Test
    public void compareTest() {
        assertFalse(appleBox1.compare(orangeBox));  // 4.0 != 6.0
        assertTrue(appleBox2.compare(orangeBox));   // 6.0 == 6.0
    }

    @Test
    public void shiftFruitsIntoAnotherBoxTest() {
        final Box<Apple> appleBox3 = createAppleBox(3);
        final Box<Apple> appleBox4 = createAppleBox(5);

        assertEquals(3.0f, appleBox3.getWeight(), 1e-3);
        assertEquals(5.0f, appleBox4.getWeight(), 1e-3);

        appleBox3.shiftFruitsIntoAnotherBox(appleBox4);

        assertEquals(0.0f, appleBox3.getWeight(), 1e-3);
        assertEquals(8.0f, appleBox4.getWeight(), 1e-3);
    }

    // Создать коробку с count яблоками
    private static Box<Apple> createAppleBox(int count) {
        final Box<Apple> appleBox = new Box<Apple>();
        for (int i = 0; i < count; i++) {
            appleBox.addFruit(new Apple());
        }
        return appleBox;
    }

    // Создать коробку с count апельсинами
    private static Box<Orange> createOrangeBox(int count) {
        final Box<Orange> orangeBox = new Box<Orange>();
        for (int i = 0; i < count; i++) {
            orangeBox.addFruit(new Orange());
        }
        return orangeBox;
    }
}
