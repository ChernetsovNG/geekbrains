package ru.nchernetsov.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Вспомогательный класс для установления соединения с базой данных
 */
public class ConnectionHelper {

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());

            String url = "jdbc:postgresql://" +  // db type
                "localhost:" +                   // host name
                "5432/" +                        // port
                "patterns-test-db?" +            // db name
                "user=postgres&" +               // login
                "password=postgres";             // password

            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void printDBInfo() {
        try (Connection connection = getConnection()) {
            System.out.println("Connected to: " + connection.getMetaData().getURL());
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
