package com.studyplanner.repository;

import com.studyplanner.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByUserIdOrderByNameAsc(Long userId);
    Optional<Subject> findByIdAndUserId(Long id, Long userId);
    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);
}
