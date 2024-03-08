package com.example.controller;

import com.example.model.Student;
import com.example.model.Course;
import com.example.model.Registrar;
import com.example.model.ErrorResponse;
import com.example.model.ErrResponse;
import com.example.model.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid; 
import java.util.Collections;


import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;



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
@Validated
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
        students.add(new Student(studentCounter.incrementAndGet(), "Tom", "Evan", LocalDate.of(1997, 9, 25), "jom@example.com"));

        // Populate courses
        courses.add(new Course(101, "Mathematics"));
        courses.add(new Course(102, "Science"));


    }

    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity index() {

        System.out.println("+++++++++++++++++++ BucketListController GET ++++++++++++++++");


        return ResponseEntity.ok(students);
    }


    // UC_S1: Instantiate Student object and populate it with data.
    @PostMapping(value = "/students", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createStudent(@RequestBody Student student, BindingResult bindingResult) {
        // Manual validation
        if (bindingResult.hasErrors()) {
            // Collect all validation errors
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            // Return the list of validation errors in JSON format
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        // Check for required fields
        if (student.getLastName() == null || student.getDateOfBirth() == null || student.getEmail() == null) {
            // If any required field is null, return a bad request response
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Last name, date of birth, and email are required fields.")));
        }

        // Perform additional validation for email format
        if (!isValidEmail(student.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Invalid email format.")));
        }

        // Perform additional validation for date of birth
        if (!isValidDateOfBirth(student.getDateOfBirth())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Date of birth must be in the past.")));
        }

        // Increment the student ID and set it to the new student
        int newStudentId = studentCounter.incrementAndGet();
        student.setStudentId(newStudentId);

        // Add the new student to the list of students
        students.add(student);

        // Return the newly created student
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }



// UC_S2: Obtain an individual Student object with a given Student_Id.
@GetMapping(value = "/students/{studentId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public ResponseEntity<?> getStudentById(@PathVariable int studentId) {
    Student student = students.stream()
            .filter(s -> s.getStudentId() == studentId)
            .findFirst()
            .orElse(null);
    if (student != null) {
        return ResponseEntity.ok(student);
    } else {
        ErrResponse errResp = new ErrResponse("Student with ID " + studentId + " cannot be found");
        return ResponseEntity.badRequest().body(errResp);
    }
}


    // UC_S3: Obtain a list of all students. Each student should be listed with all attributes.
    @GetMapping(value = "/students", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(students);
    }

    // UC_S4: Update Student object with a given Student_Id.
    @PutMapping(value = "/students/{studentId}",  produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateStudent(@RequestBody Student updatedStudent, @PathVariable Integer studentId) {
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getStudentId() == studentId) {
                students.set(i, updatedStudent);
                return ResponseEntity.ok(updatedStudent);
            }
        }
        //return ResponseEntity.notFound().build();
        ErrResponse errResp = new ErrResponse("Student with ID " + studentId + " cannot be found");
        return ResponseEntity.badRequest().body(errResp);
    }

    // UC_S5: Delete Student object with a given Student_Id.
    @DeleteMapping(value = "/students/{studentId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteStudent(@PathVariable int studentId) {
        boolean removed = students.removeIf(student -> student.getStudentId() == studentId);
        if (removed) {
            SuccessResponse successMessage = new SuccessResponse("Student with ID " + studentId + " deleted successfully");
            return ResponseEntity.ok(successMessage);
        } else {
            //return ResponseEntity.notFound().build();
            ErrResponse errResp = new ErrResponse("Student with ID " + studentId + " cannot be found");
            return ResponseEntity.badRequest().body(errResp);
        }
    }



    @PostMapping(value = "/courses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        // Check if the courseNumber is already in use
        if (isCourseNumberUnique(course.getCourseNumber())) {
             courses.add(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(course);
        } else {
            // If the courseNumber is not unique, return a conflict response
            //return ResponseEntity.status(HttpStatus.CONFLICT).build();
            ErrResponse errResp = new ErrResponse("Course " + course.getCourseNumber() + " already existed.");
            return ResponseEntity.badRequest().body(errResp);
            
        }
    }


    @GetMapping(value = "/courses/{courseNumber}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getCourse(@PathVariable int courseNumber) {
        // Find the course with the specified course number
        Course course = null;
        for (Course c : courses) {
            if (c.getCourseNumber() == courseNumber) {
                course = c;
                break;
            }
        }

        // If the course is found, return it; otherwise, return not found
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            ErrResponse errResp = new ErrResponse("Course " + courseNumber + " cannot be found");
            return ResponseEntity.badRequest().body(errResp);
            //return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/courses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        public ResponseEntity<List<Course>> getAllCourses() {
            return ResponseEntity.ok(courses);
        }


    @PutMapping(value = "/courses/{courseNumber}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateCourse(@PathVariable int courseNumber, @RequestBody Course updatedCourse) {
        // Find the course in the list
        Course existingCourse = null;
        for (Course course : courses) {
            if (course.getCourseNumber() == courseNumber) {
                existingCourse = course;
                break;
            }
        }

        // If the course does not exist, return not found
        if (existingCourse == null) {
            ErrResponse errResp = new ErrResponse("Course " + courseNumber + " cannot be found");
            return ResponseEntity.badRequest().body(errResp);
            
            //return ResponseEntity.notFound().build();
        }

        // Update the existing course with the values from updatedCourse
        existingCourse.setCourseTitle(updatedCourse.getCourseTitle());

        // Return the updated course
        return ResponseEntity.ok(existingCourse);
    }

    @DeleteMapping(value = "/courses/{courseNumber}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteCourse(@PathVariable int courseNumber) {
        int initialSize = courses.size();
        courses.removeIf(course -> course.getCourseNumber() == courseNumber);
        if (courses.size() < initialSize) {
            SuccessResponse successMessage = new SuccessResponse("Course with number " + courseNumber + " deleted successfully");
            return ResponseEntity.ok(successMessage);
        } else {
            ErrResponse errResp = new ErrResponse("Course " + courseNumber + " cannot be found");
            return ResponseEntity.badRequest().body(errResp);
            
            //return ResponseEntity.notFound().build();
       }
    }

//UC_R1: Register a given student to a given course
 @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public ResponseEntity<?> registerStudentsToCourse(@Valid @RequestBody Registrar registrar) {
    int courseNumber = registrar.getCourseNumber();
    List<Integer> studentIds = registrar.getStudentIds();

    // Find the course
    Course course = findCourseByNumber(courseNumber);
    if (course == null) {
        return ResponseEntity.notFound().build(); // Course not found
    }

    // Check if the studentIds list is not empty
    if (studentIds == null || studentIds.isEmpty()) {
        return ResponseEntity.badRequest().body("No students provided for registration.");
    }

    // Get existing student IDs registered for the course
    List<Integer> existingStudentIds = getStudentIdsForCourse(courseNumber);
    int totalRegisteredStudents = existingStudentIds.size();

    // Register each student for the course
    List<Integer> successfullyRegisteredStudents = new ArrayList<>();
    List<Integer> alreadyRegisteredStudents = new ArrayList<>();
    for (int studentId : studentIds) {
        // Find the student
        Student student = findStudentById(studentId);
        if (student == null) {
            // If a student is not found, add their ID to the list of students that could not be registered
            alreadyRegisteredStudents.add(studentId);
        } else {
            // Check if the student is already registered for this course
            if (isStudentRegisteredForCourse(studentId, courseNumber)) {
                // If the student is already registered, add their ID to the list of already registered students
                alreadyRegisteredStudents.add(studentId);
            } else {
                // Check if the course has reached its maximum capacity
                if (totalRegisteredStudents >= 15) {
                    // If the course has reached maximum capacity, break the loop
                    break;
                }

                // Register the student for the course
                addStudentToCourse(studentId, courseNumber);
                successfullyRegisteredStudents.add(studentId);
                totalRegisteredStudents++;
            }
        }
    }

    // Build the response
    StringBuilder responseMessage = new StringBuilder();
    if (!successfullyRegisteredStudents.isEmpty()) {
        responseMessage.append("Successfully registered students: ").append(successfullyRegisteredStudents).append(". ");
    }
    if (!alreadyRegisteredStudents.isEmpty()) {
        responseMessage.append("Students already registered: ").append(alreadyRegisteredStudents).append(". ");
    }
    if (totalRegisteredStudents >= 15) {
        responseMessage.append("Course has reached maximum capacity.");
    }

    if (totalRegisteredStudents < 15) {
        // All students were successfully registered or some slots are still available
        return ResponseEntity.ok().body(responseMessage.toString());
    } else {
        // Course has reached maximum capacity
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage.toString());
    }
}



@PostMapping("/registrars")
    public ResponseEntity<?> registerStudentToCourse(@RequestBody Registrar registrar) {
        registrars.add(registrar);
        return ResponseEntity.ok(registrars);
    }


// UC_R2: Obtain list of all students registered to a given course.
@GetMapping(value = "/courses/{courseNumber}/students", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public ResponseEntity<?> getStudentsRegisteredToCourse(@PathVariable int courseNumber) {
    // Get the list of student IDs registered for the course
    List<Integer> studentIds = getStudentIdsForCourse(courseNumber);

    // Retrieve the student objects based on the student IDs
    List<Student> students = new ArrayList<>();
    for (int studentId : studentIds) {
        Student student = findStudentById(studentId);
        if (student != null) {
            students.add(student);
        }
    }

    return ResponseEntity.ok(students);
}


// UC_R3: Drop a given student from a given course.
@DeleteMapping(value = "/drop", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public ResponseEntity<?> dropStudentFromCourse(@RequestParam int studentId, @RequestParam int courseNumber) {
    // Get the list of student IDs registered for the course
    List<Integer> studentIds = getStudentIdsForCourse(courseNumber);

    // Check if the student is registered for this course
    if (studentIds.contains(studentId)) {
        // Remove the student from the list of registered students
        studentIds.remove(Integer.valueOf(studentId));
        return ResponseEntity.ok().body("Student dropped from course successfully.");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student is not registered for this course.");
    }
}


    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        // Implement your email validation logic here
        // For simplicity, we'll just check if it contains '@'
        return email != null && email.contains("@");
    }

    // Utility method to validate date of birth
    private boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        // Check if the date of birth is not null and is in the past
        return dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now());
    }

    // Utility method to check if the courseNumber is unique
    private boolean isCourseNumberUnique(int courseNumber) {
        return courses.stream().noneMatch(course -> course.getCourseNumber() == courseNumber);
    }

    // Utility method to get the list of student IDs registered for a course
private List<Integer> getStudentIdsForCourse(int courseId) {
    for (Registrar registrar : registrars) {
        if (registrar.getCourseNumber() == courseId) {
            return registrar.getStudentIds();
        }
    }
    return new ArrayList<>(); // Return an empty list if no registrations found
}


private Course findCourseByNumber(int courseId) {
    for (Course course : courses) {
        if (course.getCourseNumber() == courseId) {
            return course;
        }
    }
    return null;
}

private Student findStudentById(int studentId) {
    for (Student student : students) {
        if (student.getStudentId() == studentId) {
            return student;
        }
    }
    return null;
}


private boolean isStudentRegisteredForCourse(int studentId, int courseNumber) {
    // Find the registrar for the given course
    for (Registrar registrar : registrars) {
        if (registrar.getCourseNumber() == courseNumber) {
            // Check if the student ID is in the list of registered student IDs
            return registrar.getStudentIds().contains(studentId);
        }
    }
    return false; // Student is not registered for the course
}

private void addStudentToCourse(int studentId, int courseNumber) {
    // Find the registrar for the given course
    for (Registrar registrar : registrars) {
        if (registrar.getCourseNumber() == courseNumber) {
            // Add the student ID to the list of registered student IDs
            registrar.getStudentIds().add(studentId);
            return;
        }
    }

    // If the course registrar doesn't exist, create a new one
    List<Integer> studentIds = new ArrayList<>();
    studentIds.add(studentId);
    registrars.add(new Registrar(courseNumber, studentIds));
}




}
