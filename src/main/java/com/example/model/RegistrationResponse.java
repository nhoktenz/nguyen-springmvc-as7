package com.example.model;

import java.util.List;

public class RegistrationResponse {
    private List<Integer> successfullyRegisteredStudents;
    private List<Integer> alreadyRegisteredStudents;
    private List<Integer> nonExistingStudents;
    private int totalRegisteredStudents;
    //private boolean isCourseFull;

    // Constructors
    public RegistrationResponse() {
    }

    public RegistrationResponse(List<Integer> successfullyRegisteredStudents, List<Integer> alreadyRegisteredStudents, List<Integer> nonExistingStudents, int totalRegisteredStudents, boolean isCourseFull) {
        this.successfullyRegisteredStudents = successfullyRegisteredStudents;
        this.alreadyRegisteredStudents = alreadyRegisteredStudents;
        this.nonExistingStudents = nonExistingStudents;
        this.totalRegisteredStudents = totalRegisteredStudents;
        //this.isCourseFull = isCourseFull;
    }

    // Getters and setters
    public List<Integer> getSuccessfullyRegisteredStudents() {
        return successfullyRegisteredStudents;
    }

    public void setSuccessfullyRegisteredStudents(List<Integer> successfullyRegisteredStudents) {
        this.successfullyRegisteredStudents = successfullyRegisteredStudents;
    }

    public List<Integer> getAlreadyRegisteredStudents() {
        return alreadyRegisteredStudents;
    }

    public void setAlreadyRegisteredStudents(List<Integer> alreadyRegisteredStudents) {
        this.alreadyRegisteredStudents = alreadyRegisteredStudents;
    }

    public List<Integer> getNonExistingStudents() {
        return nonExistingStudents;
    }

    public void setNonExistingStudents(List<Integer> nonExistingStudents) {
        this.nonExistingStudents = nonExistingStudents;
    }

    public int getTotalRegisteredStudents() {
        return totalRegisteredStudents;
    }

    public void setTotalRegisteredStudents(int totalRegisteredStudents) {
        this.totalRegisteredStudents = totalRegisteredStudents;
    }

    // public boolean isCourseFull() {
    //     return isCourseFull;
    // }

    // public void setCourseFull(boolean courseFull) {
    //     isCourseFull = courseFull;
    // }
}
