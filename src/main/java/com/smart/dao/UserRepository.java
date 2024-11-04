package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findByEmailAndPassword(String email, String password);
    public User findByName(String name);

}
