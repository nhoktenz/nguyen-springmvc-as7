package com.example.controller;

import com.example.model.Student;
import com.example.model.Course;
import com.example.model.Registrar;
import com.example.model.ErrorResponse;
import com.example.model.ErrResponse;
import com.example.model.SuccessResponse;
import com.example.model.RegistrationResponse;
import com.example.model.DropRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import javax.validation.Valid; 
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


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
    private static final int MAX_CAPACITY = 15;


    public SchoolController(){
        // Populate students
        // students.add(new Student(studentCounter.incrementAndGet(), "John", "Doe", LocalDate.of(1995, 10, 15), "john@example.com"));
        // students.add(new Student(studentCounter.incrementAndGet(), "Jane", "Smith", LocalDate.of(1996, 8, 25), "jane@example.com"));
        // students.add(new Student(studentCounter.incrementAndGet(), "Tom", "Evan", LocalDate.of(1997, 9, 25), "jom@example.com"));
        students.add(new Student(studentCounter.incrementAndGet(), "John", "Doe", "1995-10-15", "john@example.com"));
        students.add(new Student(studentCounter.incrementAndGet(), "Jane", "Smith", "1996-08-25", "jane@example.com"));
        students.add(new Student(studentCounter.incrementAndGet(), "Tom", "Evan", "1997-09-25", "jom@example.com"));

        // Populate courses
        courses.add(new Course(101, "Mathematics"));
        courses.add(new Course(102, "Science"));


    }

    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> index() {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("students", students);
        responseData.put("courses", courses);
        return ResponseEntity.ok(responseData);
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
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Invalid date of birth format. Please provide the date in YYYY-MM-DD format. Date of birth must be in the past.")));
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
    public ResponseEntity<?> updateStudent(@RequestBody @Valid Student updatedStudent, @PathVariable Integer studentId, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        // Check for required fields
        if (updatedStudent.getFirstName() == null || updatedStudent.getLastName() == null ||
                updatedStudent.getDateOfBirth() == null || updatedStudent.getEmail() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("First name, last name, date of birth, and email are required fields.")));
        }

        // Perform additional validation for email format
        if (!isValidEmail(updatedStudent.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Invalid email format.")));
        }

        // Perform additional validation for date of birth
        if (!isValidDateOfBirth(updatedStudent.getDateOfBirth())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Invalid date of birth format. Please provide the date in YYYY-MM-DD format. Date of birth must be in the past.")));
        }



        // Update the student
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getStudentId() == studentId) {
                // Set the studentId of the updatedStudent
                updatedStudent.setStudentId(studentId);
                // Update the fields of the existing student object
                student.setFirstName(updatedStudent.getFirstName());
                student.setLastName(updatedStudent.getLastName());
                student.setDateOfBirth(updatedStudent.getDateOfBirth());
                student.setEmail(updatedStudent.getEmail());
                // Return the updated student
                return ResponseEntity.ok(updatedStudent);
            }
        }
        // Return an error response if the student with the given ID is not found
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


    //UC_C1: Instantiate Course object and populate it with data.
   @PostMapping(value = "/courses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        // Check for required fields
        if (course.getCourseNumber() == 0 || course.getCourseTitle() == null) {
            // If any required field is null, return a bad request response
            return ResponseEntity.badRequest().body(new ErrorResponse(Collections.singletonList("Course Number and Course Title are required fields.")));
        }

        // Check if the courseNumber is already in use
        if (isCourseNumberUnique(course.getCourseNumber())) {
            courses.add(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(course);
        } else {
            // If the courseNumber is not unique, return a conflict response
            ErrResponse errResp = new ErrResponse("Course " + course.getCourseNumber() + " already existed.");
            return ResponseEntity.badRequest().body(errResp);
        }
    }


    //UC_C2: Obtain an individual Course object with given Course_Number.
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

    //UC_C3: Obtain a list of all courses. Each course should be listed with all attributes
    @GetMapping(value = "/courses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        public ResponseEntity<List<Course>> getAllCourses() {
            return ResponseEntity.ok(courses);
        }

    //UC_C4: Update Course object with a given Course_Number
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

    //UC_C5: Delete Course object with a given Course_Number.
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
            ErrResponse errResp = new ErrResponse("Course Number = " + courseNumber +" not found!");
            return ResponseEntity.badRequest().body(errResp);
            //return ResponseEntity.notFound().build(); // Course not found
        }

        // Check if the studentIds list is not empty
        if (studentIds == null || studentIds.isEmpty()) {
            ErrResponse errResp = new ErrResponse("No students provided for registration.");
            return ResponseEntity.badRequest().body(errResp);
        }

        // Check if the number of students to be registered exceeds the maximum capacity
        if (studentIds.size() > MAX_CAPACITY) {
            // If the number of students to be registered exceeds the maximum capacity, return a bad request response
            ErrResponse errResp = new ErrResponse("Exceeded maximum capacity of student's id input. Maximum capacity is " + MAX_CAPACITY);
            return ResponseEntity.badRequest().body(errResp);
        }

        // Get existing student IDs registered for the course
        List<Integer> existingStudentIds = getStudentIdsForCourse(courseNumber);
        int totalRegisteredStudents = existingStudentIds.size();

        // Check if the number of students to be registered exceeds the remaining capacity
        // int remainingCapacity = MAX_CAPACITY - totalRegisteredStudents;
        // if (studentIds.size() > remainingCapacity) {
        //     // If the number of students to be registered exceeds the remaining capacity, return a bad request response
        //     ErrResponse errResp = new ErrResponse("Exceeded remaining capacity. Maximum remaining capacity is " + remainingCapacity);
        //     return ResponseEntity.badRequest().body(errResp);
        // }

        // Register each student for the course
        List<Integer> successfullyRegisteredStudents = new ArrayList<>();
        List<Integer> alreadyRegisteredStudents = new ArrayList<>();
        List<Integer> nonExistingStudents = new ArrayList<>();
        for (int studentId : studentIds) {
            // Check if the student exists
            Student student = findStudentById(studentId);
            if (student == null) {
                // If the student does not exist, add their ID to the list of non-existing students
                nonExistingStudents.add(studentId);
            } else {
                // Check if the student is already registered for this course
                if (isStudentRegisteredForCourse(studentId, courseNumber)) {
                    // If the student is already registered, add their ID to the list of already registered students
                    alreadyRegisteredStudents.add(studentId);
                } else {
                    // Register the student for the course
                    addStudentToCourse(studentId, courseNumber);
                    successfullyRegisteredStudents.add(studentId);
                    totalRegisteredStudents++;
                }
            }
        }

        // Create RegistrationResponse object
        RegistrationResponse response = new RegistrationResponse();
        response.setSuccessfullyRegisteredStudents(successfullyRegisteredStudents);
        response.setAlreadyRegisteredStudents(alreadyRegisteredStudents);
        response.setNonExistingStudents(nonExistingStudents);
        response.setTotalRegisteredStudents(totalRegisteredStudents);
        //response.setCourseFull(totalRegisteredStudents >= MAX_CAPACITY);

        return ResponseEntity.ok().body(response);
    }


    // Additional register method to register a student to a course using URL parameters
    @PostMapping("/reg")
    public ResponseEntity<?> registerStudentToCourseByParams(@RequestParam int courseNumber, @RequestParam int studentId) {
        // Find the course
        Course course = findCourseByNumber(courseNumber);
        if (course == null) {
            return ResponseEntity.notFound().build(); // Course not found
        }

        // Find the student
        Student student = findStudentById(studentId);
        if (student == null) {
            return ResponseEntity.notFound().build(); // Student not found
        }

        // Check if the student is already registered for this course
        if (isStudentRegisteredForCourse(studentId, courseNumber)) {
            ErrResponse errResp = new ErrResponse("Student " + studentId + " is already registered for this course. " + courseNumber );
            return ResponseEntity.badRequest().body(errResp);
        }

        // Check if the course has reached its maximum capacity
        List<Integer> existingStudentIds = getStudentIdsForCourse(courseNumber);
        if (existingStudentIds.size() >= MAX_CAPACITY) {
            ErrResponse errResp = new ErrResponse("Course has reached maximum capacity.");
            return ResponseEntity.badRequest().body(errResp);
        }

        // Register the student for the course
        addStudentToCourse(studentId, courseNumber);
        SuccessResponse successMessage = new SuccessResponse("Student with ID " + studentId + " successfully registered for the course" + courseNumber);
        return ResponseEntity.ok(successMessage);
    }


    //This will return the array od student's ids of each registration course
    @GetMapping("/registrars")
       public ResponseEntity<List<Registrar>> getAllRegistrars() {
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

            SuccessResponse successMessage = new SuccessResponse("Student with ID = " + studentId + " dropped from course" + courseNumber + " successfully");
            return ResponseEntity.ok(successMessage);
        } else {
            ErrResponse errResp = new ErrResponse("Student with ID = " + studentId + " cannot be found registered for course " + courseNumber );
            return ResponseEntity.badRequest().body(errResp);
           
        }
    }

    // UC_R4: Drop a list of students from a given course.
    @DeleteMapping(value = "/drops", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> dropStudentsFromCourse(@RequestBody DropRequest dropRequest) {
        // Extract student IDs and course number from the request body
        List<Integer> studentIds = dropRequest.getStudentIds();
        int courseNumber = dropRequest.getCourseNumber();

        // Get the list of student IDs registered for the course
        List<Integer> registeredStudentIds = getStudentIdsForCourse(courseNumber);

        // Initialize lists to hold successfully dropped students and non-registered students
        List<Integer> successfullyDroppedStudents = new ArrayList<>();
        List<Integer> nonRegisteredStudents = new ArrayList<>();

        // Iterate over the provided student IDs
        for (int studentId : studentIds) {
            // Check if the student is registered for this course
            if (registeredStudentIds.contains(studentId)) {
                // Remove the student from the list of registered students
                registeredStudentIds.remove(Integer.valueOf(studentId));
                successfullyDroppedStudents.add(studentId);
            } else {
                // Add the student ID to the list of non-registered students
                nonRegisteredStudents.add(studentId);
            }
        }

        // If any students were successfully dropped, return success response
        if (!successfullyDroppedStudents.isEmpty()) {
            SuccessResponse successMessage = new SuccessResponse("Students with IDs " + successfullyDroppedStudents + " dropped from course " + courseNumber + " successfully");
            return ResponseEntity.ok(successMessage);
        } else {
            // If no students were successfully dropped, return bad request response
            ErrResponse errResp = new ErrResponse("No registered students found in the provided list for course " + courseNumber);
            return ResponseEntity.badRequest().body(errResp);
        }
    }




    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        // Check if email is not null, contains "@" and ".", and "@" appears before "."
        return email != null && email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".");
    }


    // Utility method to validate date of birth
    private boolean isValidDateOfBirth(String dateOfBirth) {

        // Define the regular expression pattern for date in "yyyy-MM-dd" format
        String datePattern = "\\d{4}-\\d{2}-\\d{2}";

        // Check if the date of birth matches the format
        if (!dateOfBirth.matches(datePattern)) {
            return false;
        }

        // Parse the date of birth string to a LocalDate object
        try {
            LocalDate parsedDate = LocalDate.parse(dateOfBirth);
            // Check if the parsed date is in the past
            return parsedDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            // Handle parsing exception (e.g., if the date is invalid)
            return false;
        }
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
