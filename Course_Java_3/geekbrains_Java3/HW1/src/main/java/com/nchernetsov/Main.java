package com.nchernetsov;

import java.util.ArrayList;

public class Main {
    // Метод, который меняем два элемента массива местами
    public static <T> void swapArrayElements(T[] array, int element1, int element2) {
        assert (element1 >= 0 && element1 < array.length);
        assert (element2 >= 0 && element2 < array.length);

        T tmp = array[element1];
        array[element1] = array[element2];
        array[element2] = tmp;
    }

    // Метод для преобразования массива в ArrayList
    public static <T> ArrayList<T> convertArrayToList(T[] array) {
        ArrayList<T> arrayList = new ArrayList<T>();

        for (T element : array) {
            arrayList.add(element);
        }

        return arrayList;
    }

    // Вспомогательный метод для печати элементов массива
    public static <T> void printArray(T[] array) {
        StringBuilder sb = new StringBuilder();
        for (T element : array) {
            sb.append(element).append(" ");
        }
        System.out.println(sb.toString());
    }
}
