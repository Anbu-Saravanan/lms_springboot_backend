//package com.lms.controller;
//
//import com.lms.controller.EnrollmentController;
//import com.lms.model.User;
//import com.lms.repository.UserRepository;
//import com.lms.responseDTO.EnrollmentResponseDTO;
//import com.lms.service.EnrollmentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//
//class EnrollmentControllerTest {
//
//    private EnrollmentController controller;
//    private EnrollmentService enrollmentService;
//    private UserRepository userRepository;
//    private Authentication authentication;
//
//    @BeforeEach
//    void setUp() {
//        enrollmentService = Mockito.mock(EnrollmentService.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        authentication = Mockito.mock(Authentication.class);
//
//        controller = new EnrollmentController();
//        // Inject mocks
//        var serviceField = EnrollmentController.class.getDeclaredFields()[0];
//        var repoField = EnrollmentController.class.getDeclaredFields()[1];
//        serviceField.setAccessible(true);
//        repoField.setAccessible(true);
//        try {
//            serviceField.set(controller, enrollmentService);
//            repoField.set(controller, userRepository);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void testGetMyEnrollments() {
//        // Mock authentication
//        Mockito.when(authentication.getName()).thenReturn("student@gmail.com");
//        // Mock user repo
//        User user = new User();
//        user.setId(1L);
//        Mockito.when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(user));
//        // Mock service
//        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
//        dto.setCourseTitle("Course 1");
//        Mockito.when(enrollmentService.getEnrollmentsByStudent(1L)).thenReturn(Arrays.asList(dto));
//
//        ResponseEntity<?> resp = controller.getMyEnrollments(authentication);
//
//        assertEquals(200, resp.getStatusCodeValue());
//        assertFalse(((java.util.List<?>)resp.getBody()).isEmpty());
//    }
//
//    // Add more test methods as needed for other endpoints
//}
