package com.lms.controller;

import com.lms.requestDTO.MediaRequestDTO;
import com.lms.responseDTO.MediaResponseDTO;
import com.lms.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/lessons/{lessonId}/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<MediaResponseDTO> addMedia(
            @PathVariable Long lessonId,
            @RequestBody MediaRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(mediaService.addMedia(lessonId, dto, principal.getName()));
    }
}
