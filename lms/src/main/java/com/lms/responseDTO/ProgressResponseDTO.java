package com.lms.responseDTO;

import com.lms.model.Progress.Status;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProgressResponseDTO {
    private Long id;
    private Long enrollmentId;
    private Long lessonId;
    private Status status;
    private LocalDateTime updatedAt;
}
