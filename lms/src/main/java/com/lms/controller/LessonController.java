package com.lms.controller;

import com.lms.requestDTO.LessonRequestDTO;
import com.lms.responseDTO.LessonResponseDTO;
import com.lms.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // Create lesson for a course
    // Create a lesson for a course
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<LessonResponseDTO> createLesson(
            @PathVariable Long courseId,
            @RequestBody LessonRequestDTO dto,
            Principal principal,
            Authentication authentication
    ) throws AccessDeniedException {
        String username = principal.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        LessonResponseDTO response = lessonService.createLesson(courseId, dto, username, isAdmin);
        return ResponseEntity.ok(response);
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")
    public ResponseEntity<List<LessonResponseDTO>> getLessonsByCourse(
            @PathVariable Long courseId,
            Principal principal,
            Authentication authentication
    ) throws AccessDeniedException {
        String username = principal.getName();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId, username, isAdmin, isInstructor));
    }

    // Get a specific lesson by its ID
    @GetMapping("/getById/{lessonId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")
    public ResponseEntity<LessonResponseDTO> getLessonById(
            @PathVariable Long courseId,
            @PathVariable Long lessonId
    ) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    // Update a lesson
    @PutMapping("/update/{lessonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<LessonResponseDTO> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody LessonRequestDTO dto
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, dto));
    }

    // Delete a lesson
    @DeleteMapping("/delete/{lessonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId
    ) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
