package com.example.catalogproject.repository;

import com.example.catalogproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserNameAndUserPassword(String userName, String userPassword);
    User findByUserName(String userName);
}