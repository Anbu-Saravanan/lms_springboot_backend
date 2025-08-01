package com.lms.responseDTO;

import lombok.Data;

@Data
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String instructorName;
    private boolean approved;
}
