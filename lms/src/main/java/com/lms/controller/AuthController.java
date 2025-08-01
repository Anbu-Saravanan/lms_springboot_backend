package com.lms.controller;

import com.lms.model.RefreshToken;
import com.lms.model.User;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.LoginRequestDTO;
import com.lms.requestDTO.RefreshTokenRequestDTO;
import com.lms.requestDTO.UserRequestDTO;
import com.lms.responseDTO.AuthenticationResponseDTO;
import com.lms.security.JwtUtil;
import com.lms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final com.lms.security.CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    // LOGIN endpoint: returns JWT on success
    @PostMapping("/login")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('STUDENT')")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginRequestDTO request) {
        // Authenticate username and password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        User user= userRepository.findByEmail(request.getEmail()).orElseThrow(() ->new UsernameNotFoundException("User not found"));
        String jwt = jwtUtil.generateToken(userDetails);

        //Create and return refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt, refreshToken.getToken(),user.getEmail(),user.getRole()));
    }

    // Simple REGISTER endpoint: create user (STUDENT by default, can be improved)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase())); // e.g., "STUDENT"
        user.setEnabled(true);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    //Add /refresh endpoint for refresh tokens

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshTokenService.isRefreshTokenExpired(refreshToken)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please login again.");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String jwt = jwtUtil.generateToken(userDetails);

        // (Optional) rotate refresh token: create a new one and delete the old

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(
                new AuthenticationResponseDTO(jwt, newRefreshToken.getToken(), user.getEmail(),user.getRole())
        );
    }

}

