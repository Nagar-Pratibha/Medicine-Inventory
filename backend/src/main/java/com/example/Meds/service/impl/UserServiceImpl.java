package com.example.Meds.service.impl;

import com.example.Meds.dto.UserDTO;
import com.example.Meds.entity.User;
import com.example.Meds.exception.ResourceNotFoundException;
import com.example.Meds.mapper.UserMapper; // Assuming you are using MapStruct
import com.example.Meds.repository.UsersRepository;
import com.example.Meds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper; // Using UserMapper for consistency

    @Autowired
    public UserServiceImpl(UsersRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public User register(UserDTO userDTO) {
        User user = userMapper.toUserEntity(userDTO);

        // Hash the password before saving
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(User.Role.USER); // Set default role securely
        return userRepository.save(user);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateProfile(Integer id, UserDTO userDTO) {
        // 1. Fetch the existing user from the database
        User existingUser = findById(id);

        // 2. Apply updates from the DTO to the existing entity
        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());

        // 3. Optionally, handle password updates
        // Only update the password if a new one is provided in the DTO
        if (StringUtils.hasText(userDTO.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 4. Save the updated entity
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

//    @Override
//    public String deleteById(int id) {
//        this.userRepository.deleteById(id);
//        return "Completed";
//    }
}