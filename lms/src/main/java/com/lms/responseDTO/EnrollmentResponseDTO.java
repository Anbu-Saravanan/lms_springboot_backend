package com.lms.responseDTO;

import lombok.Data;

@Data
public class EnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private String enrolledAt;
    private String instructorName;

}
