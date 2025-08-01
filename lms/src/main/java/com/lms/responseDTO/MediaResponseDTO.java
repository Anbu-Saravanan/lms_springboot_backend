package com.lms.responseDTO;

import com.lms.model.Media;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MediaResponseDTO {
    private Long id;
    private Long uploaderId;
    private Long lessonId;
    private String url;
    private Media.Type type;
    private LocalDateTime uploadedAt;
}
