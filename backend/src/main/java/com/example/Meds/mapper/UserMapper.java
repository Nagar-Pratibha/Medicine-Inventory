package com.example.Meds.mapper;

import com.example.Meds.dto.UserDTO;
import com.example.Meds.dto.UserResponseDTO;
import com.example.Meds.entity.User;
import org.springframework.stereotype.Component;

@Component // Make it a Spring bean
public class UserMapper {

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) return null;
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}