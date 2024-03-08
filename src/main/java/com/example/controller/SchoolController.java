package com.example.controller;

import com.example.model.Student;
import com.example.model.Course;
import com.example.model.Registrar;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;


import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schoolservice")
public class SchoolController {

    private List<Student> students = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Registrar> registrars = new ArrayList<>();
    private final AtomicInteger studentCounter = new AtomicInteger();
    private final AtomicLong courseCounter = new AtomicLong();
    private final AtomicLong registrarCounter = new AtomicLong();

    public SchoolController(){
        // Populate students
        students.add(new Student(studentCounter.incrementAndGet(), "John", "Doe", LocalDate.of(1995, 10, 15), "john@example.com"));
        students.add(new Student(studentCounter.incrementAndGet(), "Jane", "Smith", LocalDate.of(1996, 8, 25), "jane@example.com"));

        // Populate courses
        courses.add(new Course(101, "Mathematics"));
        courses.add(new Course(102, "Science"));
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity index() {

        System.out.println("+++++++++++++++++++ BucketListController GET ++++++++++++++++");


        return ResponseEntity.ok(students);
    }


    // UC_S1: Instantiate Student object and populate it with data.
    @PostMapping(value = "/students", produces = "application/json")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        students.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    // UC_S2: Obtain an individual Student object with a given Student_Id.
    @GetMapping(value = "/students/{studentId}", produces = "application/json")
    public ResponseEntity<Student> getStudentById(@PathVariable int studentId) {
        Student student = students.stream()
                .filter(s -> s.getStudentId() == studentId)
                .findFirst()
                .orElse(null);
        return student != null ?
                ResponseEntity.ok(student) :
                ResponseEntity.notFound().build();
    }
    // UC_S3: Obtain a list of all students. Each student should be listed with all attributes.
    @GetMapping(value = "/students", produces = "application/json")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(students);
    }

    // UC_S4: Update Student object with a given Student_Id.
    @PutMapping(value = "/students/{studentId}", produces = "application/json")
    public ResponseEntity<Student> updateStudent(@RequestBody Student updatedStudent, @PathVariable Integer studentId) {
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getStudentId() == studentId) {
                students.set(i, updatedStudent);
                return ResponseEntity.ok(updatedStudent);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // UC_S5: Delete Student object with a given Student_Id.
    @DeleteMapping(value = "/students/{studentId}", produces = "application/json")
    public ResponseEntity<Void> deleteStudent(@PathVariable int studentId) {
        students.removeIf(student -> student.getStudentId() == studentId);
        return ResponseEntity.noContent().build();
    }
}
