package com.nchernetsov;

import java.sql.*;

public class DBService {
    public static Connection connection;

    public static Student student1 = new Student(1, "Student1", 10);
    public static Student student2 = new Student(2, "Student2", 20);
    public static Student student3 = new Student(3, "Student3", 30);

    public static void openDBConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:databaseHW6.db");
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable() {
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

    public static void fillTable() {
        insertStudent(student1);
        insertStudent(student2);
        insertStudent(student3);
    }

    // Добавить в таблицу нового студента
    public static void insertStudent(Student student) {
        String insertStudentQuery = "INSERT INTO students (id, surname, score) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertStudentQuery)) {
            statement.setInt(1, student.getId());
            statement.setString(2, student.getSurname());
            statement.setInt(3, student.getScore());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Получить из таблицы студента по id
    public static Student getStudentById(int id) {
        String selectQuery = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String surname = resultSet.getString("surname");
                int score = resultSet.getInt("score");
                return new Student(id, surname, score);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Обновить баллы у студента с заданным id
    public static void setStudentScore(int id, int score) {
        String updateScoreQuery = "UPDATE students SET score = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateScoreQuery)) {
            statement.setInt(1, score);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Удалить студента с заданным id
    public static void deleteStudent(int id) {
        String deleteQuery = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeDatabaseConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
