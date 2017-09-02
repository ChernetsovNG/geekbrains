package com.nchernetsov;

import org.junit.*;

import java.sql.SQLException;
import java.sql.Savepoint;

import static com.nchernetsov.DBService.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DBServiceTest {
    private static Savepoint savepoint;

    @BeforeClass
    public static void createAndFillTable() {
        openDBConnection();
        createTable();
        fillTable();
        try {
            // если в тестах не делать commit, то состояние базы данные не изменится
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void closeDBConnection() {
        closeDatabaseConnection();
    }

    @After
    public void rollbackToSavepoint() {
        try {
            connection.rollback(savepoint);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertStudentsTest() {
        Student testStudent1 = new Student(10, "Alex", 50);
        Student testStudent2 = new Student(11, "Peter", 60);

        insertStudent(testStudent1);
        insertStudent(testStudent2);

        Student studentFromDB1 = getStudentById(10);
        Student studentFromDB2 = getStudentById(11);

        assertTrue(testStudent1.equals(studentFromDB1));
        assertTrue(testStudent2.equals(studentFromDB2));
    }

    @Test
    public void readStudentsTest() {
        Student studentFromDB1 = getStudentById(1);
        Student studentFromDB2 = getStudentById(2);
        Student studentFromDB3 = getStudentById(3);

        assertTrue(student1.equals(studentFromDB1));
        assertTrue(student2.equals(studentFromDB2));
        assertTrue(student3.equals(studentFromDB3));
    }

    @Test
    public void updateStudentScoreTest() {
        setStudentScore(1, 75);

        Student studentFromDB = getStudentById(1);

        assertEquals(75, studentFromDB.getScore());
    }

    @Test
    public void deleteStudentTest() {
        deleteStudent(2);
        deleteStudent(3);

        Student studentFromDB2 = getStudentById(2);
        Student studentFromDB3 = getStudentById(3);

        assertNull(studentFromDB2);
        assertNull(studentFromDB3);
    }
}


















