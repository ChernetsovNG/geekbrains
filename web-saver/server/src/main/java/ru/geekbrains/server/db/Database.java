package ru.geekbrains.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.dto.AuthStatus;
import ru.geekbrains.server.db.dto.User;

import java.sql.*;

import static ru.geekbrains.common.dto.AuthStatus.AUTH_OK;
import static ru.geekbrains.common.dto.AuthStatus.INCORRECT_PASSWORD;
import static ru.geekbrains.common.dto.AuthStatus.INCORRECT_USERNAME;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private static Connection connection;

    public static void createServerDB() {
        openDatabaseConnection();
        createAuthTable();
        // insertUser(new User("TestUser1", "qwerty"));
    }

    public static void insertUser(User user) {
        String insertUser = "INSERT INTO users (name, password) VALUES (?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(insertUser)) {
            statement.setString(1, user.getName());
            statement.setString(2, Password.hashPassword(user.getPassword()));
            statement.executeUpdate();
            user.setId(getInsertedUserId());
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    // запрашиваем у БД id последней вставленной строки
    private static int getInsertedUserId() {
        String getLastRowId = "SELECT LAST_INSERT_ROWID() FROM users";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getLastRowId);
            resultSet.next();
            return resultSet.getInt("LAST_INSERT_ROWID()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Проверяем, что пользователь с заданным именем есть в базе
    public static boolean checkUserExistence(User user) {
        String selectUser = "SELECT * FROM users WHERE name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(selectUser)) {
            statement.setString(1, user.getName());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    // Проверяем, что пользователь с заданным именем и паролем есть в базе
    public static boolean checkUserAuthentification(User user) {
        String selectUser = "SELECT password FROM users WHERE name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(selectUser)) {
            statement.setString(1, user.getName());
            ResultSet resultSet = statement.executeQuery();
            boolean isUserExists = resultSet.next();
            if (isUserExists) {
                String storedPassword = resultSet.getString("password");
                return Password.checkPassword(user.getPassword(), storedPassword);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static AuthStatus getAuthStatus(User user) {
        boolean isUserExists = checkUserExistence(user);
        AuthStatus authStatus;
        if (!isUserExists) {
            authStatus = INCORRECT_USERNAME;
        } else {
            boolean isAuthentificate = checkUserAuthentification(user);
            if (!isAuthentificate) {
                authStatus = INCORRECT_PASSWORD;
            } else {
                authStatus = AUTH_OK;
            }
        }
        return authStatus;
    }

    private static void openDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server/src/main/java/ru/geekbrains/server/db/data.db");
            // connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    private static void createAuthTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users\n" +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "  name TEXT NOT NULL," +
                "  password TEXT NOT NULL);");
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS users_name_uindex ON users (name);");
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS users_id_uindex ON users (id);");  // имя пользователя - уникальное
            // statement.execute("DELETE FROM users");
            // statement.execute("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='users';");
            // connection.commit();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public static void closeDatabaseConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }
}
