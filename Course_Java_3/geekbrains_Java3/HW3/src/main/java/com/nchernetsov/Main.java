package com.nchernetsov;

import java.io.*;
import java.util.*;

public class Main {
    private static final String DIR_NAME = "test_files";

    public static void main(String[] args) {
        new File(DIR_NAME).mkdirs();

        // 1. Прочитать файл (около 50 байт) в байтовый массив и вывести этот массив в консоль
        homeworkPart1();

        // 2. Последовательно сшить 5 файлов в один (файлы также ~100 байт)
        homeworkPart2();

        // 3. Написать консольное приложение, которое умеет постранично читать текстовые файлы (размером > 10 mb), вводим страницу,
        // программа выводит ее в консоль (за страницу можно принять 1800 символов)
        homeworkPart3();
    }

    public static void homeworkPart1() {
        createFile(DIR_NAME + "/randomByteFile1.txt", 50);
        try {
            FileInputStream fis = new FileInputStream(DIR_NAME + "/randomByteFile1.txt");
            int readByte;
            int i = 0;
            byte[] readByteArray = new byte[fis.available()];
            while ((readByte = fis.read()) != -1) {
                readByteArray[i++] = (byte) readByte;
            }
            System.out.println(new String(readByteArray));

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void homeworkPart2() {
        try {
            createFile(DIR_NAME + "/randomByteFile2_1.txt", 100);
            Thread.sleep(20);
            createFile(DIR_NAME + "/randomByteFile2_2.txt", 100);
            Thread.sleep(20);
            createFile(DIR_NAME + "/randomByteFile2_3.txt", 100);
            Thread.sleep(20);
            createFile(DIR_NAME + "/randomByteFile2_4.txt", 100);
            Thread.sleep(20);
            createFile(DIR_NAME + "/randomByteFile2_5.txt", 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis1 = new FileInputStream(DIR_NAME + "/randomByteFile2_1.txt");
            FileInputStream fis2 = new FileInputStream(DIR_NAME + "/randomByteFile2_2.txt");
            FileInputStream fis3 = new FileInputStream(DIR_NAME + "/randomByteFile2_3.txt");
            FileInputStream fis4 = new FileInputStream(DIR_NAME + "/randomByteFile2_4.txt");
            FileInputStream fis5 = new FileInputStream(DIR_NAME + "/randomByteFile2_5.txt");

            List<InputStream> list = new ArrayList<>();
            list.add(fis1);
            list.add(fis2);
            list.add(fis3);
            list.add(fis4);
            list.add(fis5);

            Enumeration<InputStream> enumeration = Collections.enumeration(list);

            SequenceInputStream sin = new SequenceInputStream(enumeration);

            int readByte;
            int i = 0;
            byte[] readByteArray = new byte[fis1.available()+fis2.available()+fis3.available()+fis4.available()+fis5.available()];
            while ((readByte = sin.read()) != -1) {
                readByteArray[i++] = (byte) readByte;
            }
            sin.close();

            // Записываем "сумму" 5 файлов в результирующий ("суммарный") файл
            FileOutputStream fos = new FileOutputStream(DIR_NAME + "/summFile2.txt");
            fos.write(readByteArray);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void homeworkPart3() {
        createFile(DIR_NAME + "/BigFile.txt", 10*1024*1024);

        int pageSize = 1800;

        Scanner sc = new Scanner(System.in);
        try {
            RandomAccessFile raf = new RandomAccessFile(DIR_NAME + "/BigFile.txt", "r");

            while (true) {
                System.out.println("Введите номер страницы для вывода (страница - 1800 символов) от 1 до " + raf.length()/pageSize);
                System.out.println("Или введите exit, чтобы завершить работу");

                String userInput = sc.nextLine();
                if (userInput.equals("exit")) {
                    break;
                }
                try {
                    int page = Integer.parseInt(userInput);

                    if (page < 1) {
                        System.out.println("Введён номер страницы меньше 1. Повторите ввод");
                        continue;
                    }

                    int position = (page - 1)*pageSize;  // позиция курсора в тексте

                    if (position > raf.length()) {
                        System.out.println("Введён слишком большой номер страницы (за границей файла). Повторите ввод");
                        continue;
                    }

                    byte[] readByteArray = new byte[pageSize];

                    raf.seek(position);
                    int readBytesCount = raf.read(readByteArray);

                    if (readBytesCount != -1) {
                        System.out.println(new String(readByteArray));
                    } else {

                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введите положительное целое число");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Записать файл со случайным содержимым заданного размера (для тестов)
    public static void createFile(String fileName, int sizeInBytes) {
        byte[] array = new byte[sizeInBytes];
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < sizeInBytes; i++) {
            if (random.nextBoolean()) {
                array[i] = (byte) (random.nextInt(90 - 65 + 1) + 65);  // A,B,C...,Z
            } else {
                array[i] = (byte) (random.nextInt(122 - 97 + 1) + 97);  // a,b,c...,z
            }

        }

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(array);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
