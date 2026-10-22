package com.example.Meds.controller;

import com.example.Meds.dto.LoginRequestDTO;
import com.example.Meds.dto.LoginResponseDTO;
import com.example.Meds.dto.UserResponseDTO;
import com.example.Meds.entity.User;
import com.example.Meds.mapper.UserMapper;
import com.example.Meds.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If authentication is successful, generate token
        // The principal object is the User object because our User entity implements UserDetails
        final UserDetails userDetails = (UserDetails) auth.getPrincipal();

        final String token = jwtUtil.generateToken(userDetails);

        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO((User) userDetails);
        return ResponseEntity.ok(new LoginResponseDTO(token, userResponseDTO));
    }
}