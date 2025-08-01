package com.lms.repository;

import com.lms.model.Enrollment;
import com.lms.model.Lesson;
import com.lms.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByEnrollmentAndLesson(Enrollment enrollment, Lesson lesson);
    List<Progress> findByEnrollment(Enrollment enrollment);
    List<Progress> findByLesson(Lesson lesson);
}
