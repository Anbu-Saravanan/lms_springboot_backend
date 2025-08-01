package com.lms.service;

import com.lms.model.*;
import com.lms.repository.*;
import com.lms.requestDTO.ProgressRequestDTO;
import com.lms.responseDTO.ProgressResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;

    // Student updates or creates their own progress
    public ProgressResponseDTO updateProgress(ProgressRequestDTO dto, String username) {
        Enrollment enrollment = enrollmentRepository.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        // Only the student can update their progress
        if (!enrollment.getStudent().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only update your own progress.");
        }

        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        // Check that lesson is in the same course as enrollment
        if (!lesson.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new RuntimeException("Lesson does not belong to the enrollment's course.");
        }

        Progress progress = progressRepository.findByEnrollmentAndLesson(enrollment, lesson)
                .orElse(new Progress());
        progress.setEnrollment(enrollment);
        progress.setLesson(lesson);
        return mapToResponseDTO(progressRepository.save(progress));
    }

    // Anyone with role can request, but check for ownership inside
    public List<ProgressResponseDTO> getProgressByEnrollment(Long enrollmentId, String username, boolean isAdmin, boolean isInstructor) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        Course course = enrollment.getCourse();

        if (isAdmin) {
            // Admin can access any enrollment
        } else if (isInstructor) {
            // Instructor: only if owns the course
            if (!course.getInstructor().getUsername().equals(username)) {
                throw new AccessDeniedException("You can only access enrollments for your own courses.");
            }
        } else {
            // Student: only their own enrollment
            if (!enrollment.getStudent().getUsername().equals(username)) {
                throw new AccessDeniedException("You can only view your own progress.");
            }
        }

        return progressRepository.findByEnrollment(enrollment).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // For admin or instructor (see annotation on controller)
    public List<ProgressResponseDTO> getProgressByLesson(Long lessonId, String username, boolean isAdmin, boolean isInstructor) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Course course = lesson.getCourse();

        if (isAdmin) {
            // Admin can view all
        } else if (isInstructor) {
            if (!course.getInstructor().getUsername().equals(username)) {
                throw new AccessDeniedException("You can only access progress for your own lessons.");
            }
        } else {
            // Student should not access this method, controller should prevent it
            throw new AccessDeniedException("Access denied.");
        }

        return progressRepository.findByLesson(lesson).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper: convert entity to DTO
    private ProgressResponseDTO mapToResponseDTO(Progress progress) {
        ProgressResponseDTO dto = new ProgressResponseDTO();
        dto.setId(progress.getId());
        dto.setEnrollmentId(progress.getEnrollment().getId());
        dto.setLessonId(progress.getLesson().getId());
        return dto;
    }
}
