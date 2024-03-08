package com.example.model;

import java.util.List;

public class Registrar {
    private int courseNumber;
    private List<Integer> studentIds;

    // Constructors, getters, and setters
    public Registrar() {
    }

    public Registrar(int courseNumber, List<Integer> studentIds) {
        this.courseNumber = courseNumber;
        this.studentIds = studentIds;
    }

    // Getters and setters
    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public List<Integer> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }
}
