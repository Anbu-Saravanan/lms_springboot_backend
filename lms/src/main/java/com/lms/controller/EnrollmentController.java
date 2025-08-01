package com.lms.controller;

import com.lms.model.User;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.EnrollmentRequestDTO;
import com.lms.responseDTO.EnrollmentResponseDTO;
import com.lms.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserRepository userRepository;

    // Enroll a student in a course
    @PostMapping("/{courseId}/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(
            @PathVariable Long courseId,
            @RequestBody EnrollmentRequestDTO dto,
            Authentication authentication
    ) {
        // Get student from authentication, not from DTO
        String email = authentication.getName();
        User student = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Make sure courseId is not null
        if (courseId == null) throw new IllegalArgumentException("Course ID must not be null");

        return ResponseEntity.ok(enrollmentService.enrollStudent(courseId, student.getId()));
    }

    // List all enrollments for a course (admin/instructor)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByCourse(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    // Unenroll (delete enrollment)// Instructor only can remove
    @DeleteMapping("/delete/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long courseId,
            @PathVariable Long enrollmentId,
            Principal principal,
            Authentication authentication // Spring Security authentication object
    ) throws AccessDeniedException {
        String username = principal.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        enrollmentService.deleteEnrollment(enrollmentId, username, role);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getMyEnrollments(Authentication authentication) {
        String email = authentication.getName();
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(userId));
    }


}
