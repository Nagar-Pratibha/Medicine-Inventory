package com.example.Meds.repository;

import com.example.Meds.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail(String email);
}




