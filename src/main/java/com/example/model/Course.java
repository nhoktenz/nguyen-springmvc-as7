package com.example.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Objects;

public class Course {
    @Positive(message = "Course number must be positive")
    private int courseNumber;

    @NotBlank(message = "Course title is required")
    private String courseTitle;

    // Constructors, getters, and setters
    public Course() {
    }

    public Course(int courseNumber, String courseTitle) {
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
    }

    // Getters and setters
    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseNumber == course.courseNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseNumber);
    }
}
