package com.lms.requestDTO;

import com.lms.model.Media;
import lombok.Data;

@Data
public class MediaRequestDTO {
    private String url;
    private Media.Type type; // Use enum
}
