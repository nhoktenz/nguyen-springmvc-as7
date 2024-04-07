package com.example.service;

import com.example.jpa.Student;
import com.example.jpa.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service("studentService")
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Override
    public String login(String username, String password) {
 
        System.out.println ("in StudentServiceImpl - method login .... " + " username = " + username +  "   password =  " + password);

        Optional<Student> student = studentRepository.login(username,password);

        System.out.println("???????????????????????? student returned = " + student.toString());

        if(student.isPresent()){
            String token = UUID.randomUUID().toString();
            Student custom= student.get();
            custom.setToken(token);
            studentRepository.save(custom);

            System.out.println("%%%%%%%%%%%%%%%%%%%% token is set = " + token);

            return token;
        }

        return StringUtils.EMPTY;
    }

    @Override
    public Optional<User> findByToken(String token) {
        Optional<Student> student= studentRepository.findByToken(token);
        if(student.isPresent()){
            Student student1 = student.get();
            User user= new User(student1.getUserName(), student1.getPassword(), true, true, true, true,
                    AuthorityUtils.createAuthorityList("USER"));
            return Optional.of(user);
        }
        return  Optional.empty();
    }

    @Override
    public Student findById(Long id) {

        System.out.println("********************    " + "in StudentServiceImpl, findByID " + "id= " + id);
        Optional<Student> student= studentRepository.findById(id);

        System.out.println("%%%%%%%%%%%%%  " + "student  = " + student.toString());
        
        return student.orElse(null);
    }
}
