package com.lms.service;

import com.lms.model.Course;
import com.lms.model.Enrollment;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.EnrollmentRepository;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.EnrollmentRequestDTO;
import com.lms.responseDTO.EnrollmentResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    // Enroll a student in a course
    public EnrollmentResponseDTO enrollStudent(Long courseId,  Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (enrollmentRepository.findByStudentAndCourse(student, course).isPresent()) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        // enrolledAt is set by default in the entity

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponseDTO(saved);
    }

    // List all enrollments for a course (for admin/instructor)
    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return enrollmentRepository.findByCourse(course).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // List all enrollments for a student
    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return enrollmentRepository.findByStudent(student).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Delete enrollment (unenroll)
    public void deleteEnrollment(Long enrollmentId, String username, String role) throws AccessDeniedException {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));

        // If student, can only delete their own enrollment
        if (role.equals("ROLE_STUDENT")) {
            if (!enrollment.getStudent().getUsername().equals(username)) {
                throw new AccessDeniedException("Students can only delete their own enrollments.");
            }
        }

        // If instructor, can only delete from their own course
        if (role.equals("ROLE_INSTRUCTOR")) {
            if (!enrollment.getCourse().getInstructor().getUsername().equals(username)) {
                throw new AccessDeniedException("Instructors can only remove from their courses.");
            }
        }

        // Admin can delete anything (no check)

        enrollmentRepository.deleteById(enrollmentId);
    }


    private EnrollmentResponseDTO mapToResponseDTO(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentName(enrollment.getStudent().getUsername());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseTitle(enrollment.getCourse().getTitle());
        dto.setEnrolledAt(enrollment.getEnrolledAt().toString());
        dto.setInstructorName(enrollment.getCourse().getInstructor().getUsername());
        return dto;
    }
}
