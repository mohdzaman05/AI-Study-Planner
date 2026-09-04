package com.studyplanner.repository;

import com.studyplanner.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByUserIdOrderByExamDateAsc(Long userId);
    Optional<Exam> findByIdAndUserId(Long id, Long userId);
}
