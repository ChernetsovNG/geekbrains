package ru.geekbrains.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.dto.ConnectStatus;
import ru.geekbrains.common.dto.UserDTO;

import java.sql.*;

import static ru.geekbrains.common.dto.ConnectStatus.*;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private static final String INSERT_USER = "INSERT INTO users (name, password) VALUES (?, ?);";
    private static final String GET_LAST_ROW_ID = "SELECT LAST_INSERT_ROWID() FROM users";
    private static final String SELECT_USER = "SELECT * FROM users WHERE name = ?;";
    private static final String SELECT_PASSWORD = "SELECT password FROM users WHERE name = ?;";

    private static Connection connection;

    public static void createServerDB() {
        openDatabaseConnection();
        createAuthTable();
        // insertUser(new UserDTO("TestUser1", "qwerty"));
    }

    public static boolean insertUser(UserDTO user) {
        LOG.info("Insert new user in database: {}", user);

        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER)) {
            statement.setString(1, user.getName());
            statement.setString(2, Password.hashPassword(user.getPassword()));
            statement.executeUpdate();
            user.setId(getInsertedUserId());
            return true;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    // запрашиваем у БД id последней вставленной строки
    private static int getInsertedUserId() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(GET_LAST_ROW_ID);
            resultSet.next();
            return resultSet.getInt("LAST_INSERT_ROWID()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Проверяем, что пользователь с заданным именем есть в базе
    public static boolean checkUserExistence(UserDTO user) {
        LOG.info("Check user existence in database: {}", user);
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER)) {
            statement.setString(1, user.getName());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    // Проверяем, что пользователь с заданным именем и паролем есть в базе
    private static boolean checkUserPassword(UserDTO user) {
        LOG.info("Check user password in database: {}", user);
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PASSWORD)) {
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

    public static ConnectStatus getRegistrationAndAuthStatus(UserDTO user) {
        boolean isUserExists = checkUserExistence(user);
        LOG.debug("isUserExists: {}", isUserExists);
        ConnectStatus authStatus;
        if (!isUserExists) {
            authStatus = NOT_REGISTER;
        } else {
            boolean isAuthentificate = checkUserPassword(user);
            LOG.debug("isAuthentificate: {}", isAuthentificate);
            if (!isAuthentificate) {
                authStatus = INCORRECT_PASSWORD;
            } else {
                authStatus = AUTH_OK;
            }
        }
        return authStatus;
    }

    private static void openDatabaseConnection() {
        LOG.info("Start connection with database");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server/src/main/java/ru/geekbrains/server/db/data.db");
            // connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    private static void createAuthTable() {
        LOG.info("Create table usert in database (if it is not exists)");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users\n" +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "  name TEXT NOT NULL," +
                "  password TEXT NOT NULL, " +
                "  CHECK(name <> '')," +
                "  CHECK(password <> ''));");
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
        LOG.info("Close database connection");
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }
}
