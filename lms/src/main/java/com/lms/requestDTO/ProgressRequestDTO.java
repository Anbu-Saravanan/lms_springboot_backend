package com.lms.requestDTO;

import com.lms.model.Progress.Status;
import lombok.Data;

@Data
public class ProgressRequestDTO {
    private Long enrollmentId;
    private Long lessonId;
    private Status status; // NOT_STARTED, IN_PROGRESS, COMPLETED
}
