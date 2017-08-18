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
            if (command.equals(Commands.COST.getCommand())) {
                String name = strings[1];
                if (checkProductExistance(name)) {
                    System.out.println(getCostByName(name));
                } else {
                    System.out.println("Такого товара нет в базе данных");
                }
            // Изменить стоимость товара с заданным именем
            } else if (command.equals(Commands.CHANGE_COST.getCommand())) {
                String productName = strings[1];
                int newCost = Integer.parseInt(strings[2]);
                if (checkProductExistance(productName)) {
                    changeCostProductByName(productName, newCost);
                    System.out.println("Стоимость продукта изменена");
                } else {
                    System.out.println("Такого товара нет в базе данных");
                }
            // Вывести список продуктов со стоимостью в заданном диапазоне
            } else if (command.equals(Commands.GET_BY_COST.getCommand())) {
                int cost1 = Integer.parseInt(strings[1]);
                int cost2 = Integer.parseInt(strings[2]);
                getProductsByCost(cost1, cost2);
            } else if (command.equals(Commands.EXIT.getCommand())) {
                break;
            } else {
                System.out.println("Введена неправильная команда. Повторите ввод");
            }
        }

        closeDatabaseConnection();
    }

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

    // Заполняем таблицу тестовыми значениями
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

    public enum Commands {
        COST("/cost"),
        CHANGE_COST("/change_cost"),
        GET_BY_COST("/getProductsByCost"),
        EXIT("/exit");

        private final String command;

        Commands(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }
}
