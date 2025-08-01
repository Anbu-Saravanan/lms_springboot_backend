//package com.lms.controller;
//
//import com.lms.controller.AuthController;
//import com.lms.model.RefreshToken;
//import com.lms.model.User;
//import com.lms.repository.UserRepository;
//import com.lms.requestDTO.LoginRequestDTO;
//import com.lms.requestDTO.RefreshTokenRequestDTO;
//import com.lms.requestDTO.UserRequestDTO;
//import com.lms.responseDTO.AuthenticationResponseDTO;
//import com.lms.security.JwtUtil;
//import com.lms.service.RefreshTokenService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//
//class AuthControllerTest {
//
//    private AuthController controller;
//    private AuthenticationManager authenticationManager;
//    private JwtUtil jwtUtil;
//    private com.lms.security.CustomUserDetailsService userDetailsService;
//    private UserRepository userRepository;
//    private PasswordEncoder passwordEncoder;
//    private RefreshTokenService refreshTokenService;
//
//    @BeforeEach
//    void setUp() {
//        authenticationManager = Mockito.mock(AuthenticationManager.class);
//        jwtUtil = Mockito.mock(JwtUtil.class);
//        userDetailsService = Mockito.mock(com.lms.security.CustomUserDetailsService.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        passwordEncoder = Mockito.mock(PasswordEncoder.class);
//        refreshTokenService = Mockito.mock(RefreshTokenService.class);
//
//        controller = new AuthController(authenticationManager, jwtUtil, userDetailsService, userRepository, passwordEncoder, refreshTokenService);
//    }
//
//    @Test
//    void testLogin() {
//        LoginRequestDTO req = new LoginRequestDTO();
//        req.setEmail("test@example.com");
//        req.setPassword("password");
//
//        Authentication mockAuth = Mockito.mock(Authentication.class);
//        UserDetails userDetails = Mockito.mock(UserDetails.class);
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setRole(User.Role.STUDENT);
//
//        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
//        Mockito.when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
//        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt");
//        RefreshToken token = new RefreshToken();
//        token.setToken("refresh-token");
//        token.setUser(user);
//        Mockito.when(refreshTokenService.createRefreshToken(user)).thenReturn(token);
//
//        ResponseEntity<AuthenticationResponseDTO> response = controller.login(req);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("fake-jwt", response.getBody().getToken());
//        assertEquals("refresh-token", response.getBody().getRefreshToken());
//        assertEquals("test@example.com", response.getBody().getEmail());
//        assertEquals(User.Role.STUDENT, response.getBody().getRole());
//    }
//
//    @Test
//    void testRegisterNewUser() {
//        UserRequestDTO req = new UserRequestDTO();
//        req.setUsername("testuser");
//        req.setEmail("test@example.com");
//        req.setPassword("pass");
//        req.setRole("STUDENT");
//
//        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
//        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//        Mockito.when(passwordEncoder.encode("pass")).thenReturn("encoded");
//
//        ResponseEntity<String> response = controller.register(req);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("User registered successfully", response.getBody());
//    }
//
//    @Test
//    void testRefreshToken() {
//        RefreshTokenRequestDTO req = new RefreshTokenRequestDTO();
//        req.setRefreshToken("refresh-token");
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setRole(User.Role.STUDENT);
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setToken("refresh-token");
//        refreshToken.setUser(user);
//
//        UserDetails userDetails = Mockito.mock(UserDetails.class);
//
//        Mockito.when(refreshTokenService.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));
//        Mockito.when(refreshTokenService.isRefreshTokenExpired(refreshToken)).thenReturn(false);
//        Mockito.when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
//        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn("new-jwt");
//        RefreshToken newRefresh = new RefreshToken();
//        newRefresh.setToken("new-refresh-token");
//        newRefresh.setUser(user);
//        Mockito.when(refreshTokenService.createRefreshToken(user)).thenReturn(newRefresh);
//
//        ResponseEntity<?> response = controller.refresh(req);
//
//        assertEquals(200, response.getStatusCodeValue());
//        AuthenticationResponseDTO respDto = (AuthenticationResponseDTO) response.getBody();
//        assertEquals("new-jwt", respDto.getToken());
//        assertEquals("new-refresh-token", respDto.getRefreshToken());
//    }
//}
