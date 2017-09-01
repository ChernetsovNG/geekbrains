package com.nchernetsov;

import java.sql.*;

public class DBService {
    private static Connection connection;

    public static void main(String[] args) {
        openDBConnection();

        createTable();

        Student student1 = new Student(1, "Student1", 10);
        Student student2 = new Student(2, "Student2", 20);
        Student student3 = new Student(3, "Student3", 30);

        insertStudent(student1);
        insertStudent(student2);
        insertStudent(student3);

        closeDatabaseConnection();
    }

    private static void openDBConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:databaseHW6.db");
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS students " +
                "(" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "surname TEXT NOT NULL, " +
                "score INTEGER" +
                ");");
            statement.execute("DELETE FROM students;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertStudent(Student student) {
        String insertRecordQuery = "INSERT INTO students (id, surname, score) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertRecordQuery)) {
            statement.setInt(1, student.getId());
            statement.setString(2, student.getSurname());
            statement.setInt(3, student.getScore());
            statement.execute();
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
