package com.example.Meds.service;

import com.example.Meds.dto.UserDTO;
import com.example.Meds.entity.User;
import java.util.List;

public interface UserService {
    User register(UserDTO userDTO);
    User findById(Integer id);
    User updateProfile(Integer id, UserDTO updatedUser);
    List<User> getAllUsers();

//    String deleteById(int id);
}