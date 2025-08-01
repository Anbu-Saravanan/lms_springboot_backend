package com.lms.service;

import com.lms.model.User;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.UserRequestDTO;
import com.lms.responseDTO.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder; // Optional, if using password hashing
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Uncomment if you want password hashing
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Hash password if using security
        // user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPassword(dto.getPassword());
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));
        user.setEnabled(dto.isEnabled());

        User saved = userRepository.save(user);
        return mapToResponseDTO(saved);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Only set password if provided (not blank)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            // user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPassword(dto.getPassword());
        }
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));
        user.setEnabled(dto.isEnabled());

        User saved = userRepository.save(user);
        return mapToResponseDTO(saved);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Helper to map entity to response DTO
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
}
