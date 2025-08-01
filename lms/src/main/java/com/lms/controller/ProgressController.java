package com.lms.controller;

import com.lms.requestDTO.ProgressRequestDTO;
import com.lms.responseDTO.ProgressResponseDTO;
import com.lms.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    // Update or create progress
    @PostMapping("/update")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProgressResponseDTO> updateProgress(
            @RequestBody ProgressRequestDTO dto,
            Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(progressService.updateProgress(dto, username));
    }


    // Get progress for an enrollment (student in course)
    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")
    public ResponseEntity<List<ProgressResponseDTO>> getProgressByEnrollment(
            @PathVariable Long enrollmentId,
            Principal principal,
            Authentication authentication
    ) {
        String username = principal.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
        return ResponseEntity.ok(progressService.getProgressByEnrollment(enrollmentId, username, isAdmin, isInstructor));
    }

    // Get progress for a lesson (all enrollments)
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ResponseEntity<List<ProgressResponseDTO>> getProgressByLesson(
            @PathVariable Long lessonId,
            Principal principal,
            Authentication authentication
    ) {
        String username = principal.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
        return ResponseEntity.ok(progressService.getProgressByLesson(lessonId, username, isAdmin, isInstructor));
    }
}
