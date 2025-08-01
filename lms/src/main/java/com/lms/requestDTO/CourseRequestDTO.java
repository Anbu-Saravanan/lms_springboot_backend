package com.lms.requestDTO;

import lombok.Data;

@Data
public class CourseRequestDTO {
    private String title;
    private String description;
    private Long instructorId;
}
