package ru.geekbrains.server.db;

import org.mindrot.jbcrypt.BCrypt;

public class Password {
    private static final int workload = 12;

    public static String hashPassword(String passwordPlaintext) {
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(passwordPlaintext, salt);
    }

    public static boolean checkPassword(String passwordPlaintext, String storedHash) {
        boolean passwordIsVerified;

        if (null == storedHash || !storedHash.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }

        passwordIsVerified = BCrypt.checkpw(passwordPlaintext, storedHash);

        return passwordIsVerified;
    }
}
