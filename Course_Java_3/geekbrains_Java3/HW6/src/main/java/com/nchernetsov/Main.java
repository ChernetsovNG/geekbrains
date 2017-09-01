package com.nchernetsov;

public class Main {
    public static int[] method1(int[] array) {
        int fourIndex = -1;
        int lastIndex = array.length - 1;
        // Идём с конца и ищем последнюю 4
        for (int i = lastIndex; i >= 0; i--) {
            if (array[i] == 4) {
                fourIndex = i;
                break;
            }
        }
        int[] result;
        if (fourIndex < 0) {
            throw new RuntimeException("Input array not contains 4");
        } else if (fourIndex == lastIndex) {
            result = new int[0];
            return result;
        } else {
            int resultLength = lastIndex - fourIndex;
            result = new int[resultLength];
            System.arraycopy(array, fourIndex + 1, result, 0, resultLength);
            return result;
        }
    }

    public static boolean method2(int[] array) {
        if (array.length == 0) {
            return false;
        }

        int count1 = 0;  // кол-во единиц и четвёрок
        int count4 = 0;
        for (int element : array) {
            if (element != 1) {
                if (element != 4) {
                    return false;
                } else {
                    count4++;
                }
            } else {
                count1++;
            }
        }

        return count1 != 0 && count4 != 0;
    }
}
