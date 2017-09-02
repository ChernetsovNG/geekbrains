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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (score != student.score) return false;
        return surname.equals(student.surname);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + surname.hashCode();
        result = 31 * result + score;
        return result;
    }
}
