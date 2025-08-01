package com.lms.service;

import com.lms.model.Course;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.CourseRequestDTO;
import com.lms.responseDTO.CourseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        // Get the email (username) from the authenticated JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = auth.getName();
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setInstructor(instructor);
        course.setApproved(false); // Only admin can approve

        Course saved = courseRepository.save(course);
        return mapToResponseDTO(saved);
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (dto.getTitle() != null) course.setTitle(dto.getTitle());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());

        if (dto.getInstructorId() != null) {
            User instructor = userRepository.findById(dto.getInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            course.setInstructor(instructor);
        }

        return mapToResponseDTO(courseRepository.save(course));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public CourseResponseDTO approveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(true);
        return mapToResponseDTO(courseRepository.save(course));
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setInstructorName(course.getInstructor().getUsername());
        dto.setApproved(course.isApproved());
        return dto;
    }
}
