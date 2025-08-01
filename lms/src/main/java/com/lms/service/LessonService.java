package com.lms.service;

import com.lms.model.Course;
import com.lms.model.Lesson;
import com.lms.model.Media;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.LessonRepository;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.LessonRequestDTO;
import com.lms.requestDTO.MediaRequestDTO;
import com.lms.responseDTO.LessonResponseDTO;
import com.lms.responseDTO.MediaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    public LessonResponseDTO createLesson(Long courseId, LessonRequestDTO dto,
                                          String email, boolean isAdmin)
            throws AccessDeniedException {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!isAdmin && !course.getInstructor().getUsername().equals(email)) {
            throw new AccessDeniedException("You can only add lessons to your own courses.");
        }


        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setContent(dto.getContent());
        lesson.setOrderNumber(dto.getOrderNumber());
        lesson.setCourse(course); // <-- Don't forget this!

        System.out.println("Finding uploader for username: " + email);
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Uploader not found"));

        if (dto.getMediaList() != null) {
            List<Media> mediaEntities = dto.getMediaList().stream()
                    .map(mediaDto -> {
                        Media media = new Media();
                        media.setUrl(mediaDto.getUrl());
                        media.setType(mediaDto.getType());
                        media.setLesson(lesson);
                        media.setUploader(uploader); // <-- Set uploader here
                        return media;
                    })
                    .collect(Collectors.toList());
            lesson.setMediaList(mediaEntities);
        } else {
            lesson.setMediaList(new ArrayList<>());
        }

        return mapToResponseDTO(lessonRepository.save(lesson));
    }


    public List<LessonResponseDTO> getLessonsByCourse(Long courseId, String username,
                                                      boolean isAdmin, boolean isInstructor)
            throws AccessDeniedException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (isAdmin) {
            // allow
        } else if (isInstructor && course.getInstructor().getUsername().equals(username)) {
            // allow
        } else {
            // check if student is enrolled
            boolean enrolled = course.getEnrollments().stream()
                    .anyMatch(enr -> enr.getStudent().getUsername().equals(username));
            if (!enrolled) throw new AccessDeniedException("Not enrolled in this course");
        }

        return lessonRepository.findByCourse(course).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public LessonResponseDTO getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return mapToResponseDTO(lesson);
    }

    public LessonResponseDTO updateLesson(Long lessonId, LessonRequestDTO dto) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lesson.setTitle(dto.getTitle());
        lesson.setContent(dto.getContent());
        lesson.setOrderNumber(dto.getOrderNumber());

        // Handle media
        // Option 1: FULL REPLACE (clear all and add new)
        if (dto.getMediaList() != null) {
            lesson.getMediaList().clear();
            for (MediaRequestDTO mediaDto : dto.getMediaList()) {
                Media media = new Media();
                media.setUrl(mediaDto.getUrl());
                media.setType(mediaDto.getType());
                media.setLesson(lesson);
                // Set uploader if needed
                lesson.getMediaList().add(media);
            }
        }

        return mapToResponseDTO(lessonRepository.save(lesson));
    }


    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    private LessonResponseDTO mapToResponseDTO(Lesson lesson) {
        LessonResponseDTO dto = new LessonResponseDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContent(lesson.getContent());
        dto.setOrderNumber(lesson.getOrderNumber());
        dto.setCourseId(lesson.getCourse().getId());

        // Map mediaList to DTOs
        List<MediaResponseDTO> mediaDTOs = lesson.getMediaList().stream()
                .map(this::mapToMediaResponseDTO)
                .collect(Collectors.toList());
        dto.setMediaList(mediaDTOs);

        return dto;
    }
    // Copy this method here:
    private MediaResponseDTO mapToMediaResponseDTO(Media media) {
        MediaResponseDTO dto = new MediaResponseDTO();
        dto.setId(media.getId());
        dto.setUrl(media.getUrl());
        dto.setType(media.getType());
        dto.setUploaderId(media.getUploader().getId());
        dto.setLessonId(media.getLesson().getId());
        dto.setUploadedAt(media.getUploadedAt());
        return dto;
    }

}
