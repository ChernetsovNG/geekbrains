package com.nchernetsov;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        openDatabaseConnection();

        createTable();
        fillTable();

        // Запускаем диалог с пользователем
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Введите запрос к базе данных");
            System.out.println("/cost productName; /change_cost productName newCost; /getProductsByCost cost1 cost2; /exit");

            String userInput = scanner.nextLine();

            String[] strings = userInput.split(" ");
            String command = strings[0];

            // Получить стоимость товара по его имени
            if (command.equals("/cost")) {
                if (strings.length > 2) {
                    System.out.println("Введена неправильная команда. Повторите ввод");
                    continue;
                }
                String name = strings[1];
                if (checkProductExistance(name)) {
                    System.out.println(getCostByName(name));
                } else {
                    System.out.println("Такого товара нет в базе данных");
                }
            // Изменить стоимость товара с заданным именем
            } else if (command.equals("/change_cost")) {
                if (strings.length > 3) {
                    System.out.println("Введена неправильная команда. Повторите ввод");
                    continue;
                }
                String productName = strings[1];
                try {
                    int newCost = Integer.parseInt(strings[2]);
                    if (checkProductExistance(productName)) {
                        changeCostProductByName(productName, newCost);
                        System.out.println("Стоимость продукта изменена");
                    } else {
                        System.out.println("Такого товара нет в базе данных");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введите в качестве новой стоимости целое число");
                }
            // Вывести список продуктов со стоимостью в заданном диапазоне
            } else if (command.equals("/getProductsByCost")) {
                if (strings.length > 3) {
                    System.out.println("Введена неправильная команда. Повторите ввод");
                    continue;
                }
                try {
                    int cost1 = Integer.parseInt(strings[1]);
                    int cost2 = Integer.parseInt(strings[2]);
                    if (cost1 > cost2) {
                        System.out.println("Введите два целых числа cost1 <= cost2");
                        continue;
                    }
                    getProductsByCost(cost1, cost2);
                } catch (NumberFormatException e) {
                    System.out.println("Введите два целых числа cost1 <= cost2");
                }
            } else if (command.equals("/exit")) {
                break;
            } else {
                System.out.println("Введена неправильная команда. Повторите ввод");
            }
        }

        closeDatabaseConnection();
    }

    // Проверяем, что продукт с заданным именем есть в базе
    private static boolean checkProductExistance(String productName) {
        String changeCost = "SELECT * FROM products WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(changeCost)) {
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Вернуть стоимость продукта по его имени
    private static int getCostByName(String name) {
        String selectCost = "SELECT cost FROM products WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectCost)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("cost");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Изменить стоимость продукта с заданным именем
    private static void changeCostProductByName(String productName, int newCost) {
        String changeCost = "UPDATE products SET cost = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(changeCost)) {
            statement.setInt(1, newCost);
            statement.setString(2, productName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Найти продукты, стоимость которых находится в пределах диапазона
    private static void getProductsByCost(int cost1, int cost2) {
        String productsByCost = "SELECT * FROM products WHERE cost >= ? AND cost <= ?";
        try (PreparedStatement statement = connection.prepareStatement(productsByCost)) {
            statement.setInt(1, cost1);
            statement.setInt(2, cost2);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int prodid = resultSet.getInt("prodid");
                String productName = resultSet.getString("title");
                int cost = resultSet.getInt("cost");
                System.out.println("id = " + id + "; prodid = " + prodid + "; title = " + productName + "; cost = " + cost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void openDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:databaseHW2.db");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS products " +
                "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "prodid INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "cost INTEGER NOT NULL" +
                ");");
            statement.execute("DELETE FROM products");
            statement.execute("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='products';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Заполнить таблицу тестовыми значениями
    private static void fillTable() {
        String insertRecordQuery = "INSERT INTO products (prodid, title, cost) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertRecordQuery)) {
            for (int i = 1; i <= 10000; i++) {
                statement.setInt(1, i);
                statement.setString(2, "product" + i);
                statement.setInt(3, i*10);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeDatabaseConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
