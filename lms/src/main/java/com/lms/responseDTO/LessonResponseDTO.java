package com.lms.responseDTO;

import lombok.Data;

import java.util.List;

@Data
public class LessonResponseDTO {
    private Long id;
    private String title;
    private String content;
    private List<MediaResponseDTO> mediaList;
    private Integer orderNumber;
    private Long courseId;
}
