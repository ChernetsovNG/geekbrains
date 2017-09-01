package com.nchernetsov;

public class Student {
    private final int id;
    private final String surname;
    private int score;

    public Student(int id, String surname, int score) {
        this.id = id;
        this.surname = surname;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
