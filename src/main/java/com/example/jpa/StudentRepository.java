package com.example.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

    @Query(value = "SELECT u FROM Student u where u.userName = ?1 and u.password = ?2 ")

    Optional<Student> login(String username,String password);

    Optional<Student> findByToken(String token);
}
