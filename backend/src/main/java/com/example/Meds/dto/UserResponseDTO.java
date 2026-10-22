package com.example.Meds.dto;

import com.example.Meds.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private User.Role role;
}

