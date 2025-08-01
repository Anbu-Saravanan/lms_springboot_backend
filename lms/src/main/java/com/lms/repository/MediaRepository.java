package com.lms.repository;

import com.lms.model.Lesson;
import com.lms.model.Media;
import com.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByLesson(Lesson lesson);
    List<Media> findByUploader(User uploader);
}
