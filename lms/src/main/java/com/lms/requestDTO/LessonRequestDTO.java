package com.lms.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class LessonRequestDTO {
    private String title;
    private String content;
    private List<MediaRequestDTO> mediaList;
    private Integer orderNumber;
}
