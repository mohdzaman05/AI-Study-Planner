package com.studyplanner.repository;

import com.studyplanner.model.PlanStatus;
import com.studyplanner.model.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    List<StudyPlan> findByUserIdOrderByGeneratedAtDesc(Long userId);
    Optional<StudyPlan> findFirstByUserIdAndStatusOrderByGeneratedAtDesc(Long userId, PlanStatus status);
    Optional<StudyPlan> findByIdAndUserId(Long id, Long userId);
}
