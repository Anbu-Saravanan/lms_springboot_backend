package com.lms.service;

import com.lms.model.Lesson;
import com.lms.model.Media;
import com.lms.model.User;
import com.lms.repository.LessonRepository;
import com.lms.repository.MediaRepository;
import com.lms.repository.UserRepository;
import com.lms.requestDTO.MediaRequestDTO;
import com.lms.responseDTO.MediaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;

    public MediaResponseDTO addMedia(Long lessonId, MediaRequestDTO dto, String uploaderEmail) {
        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new RuntimeException("Uploader not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Media media = new Media();
        media.setUploader(uploader);
        media.setLesson(lesson);
        media.setUrl(dto.getUrl());
        media.setType(dto.getType());
        media.setUploadedAt(java.time.LocalDateTime.now());

        Media saved = mediaRepository.save(media);
        return mapToResponseDTO(saved);
    }


    private MediaResponseDTO mapToResponseDTO(Media media) {
        MediaResponseDTO dto = new MediaResponseDTO();
        dto.setId(media.getId());
        dto.setUploaderId(media.getUploader().getId());
        dto.setLessonId(media.getLesson().getId());
        dto.setUrl(media.getUrl());
        dto.setType(media.getType());
        dto.setUploadedAt(media.getUploadedAt());
        return dto;
    }
}
