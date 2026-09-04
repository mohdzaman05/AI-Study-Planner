package com.studyplanner.repository;

import com.studyplanner.model.StudyTask;
import com.studyplanner.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    List<StudyTask> findByStudyPlanIdOrderByTaskDateAscStartTimeAsc(Long studyPlanId);

    @Query("SELECT t FROM StudyTask t WHERE t.id = :taskId AND t.studyPlan.user.id = :userId")
    Optional<StudyTask> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT t FROM StudyTask t WHERE t.studyPlan.user.id = :userId AND t.taskDate = :taskDate ORDER BY t.startTime ASC")
    List<StudyTask> findByUserIdAndTaskDate(@Param("userId") Long userId, @Param("taskDate") LocalDate taskDate);

    @Query("SELECT t FROM StudyTask t WHERE t.studyPlan.user.id = :userId ORDER BY t.taskDate DESC, t.startTime ASC")
    List<StudyTask> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM StudyTask t WHERE t.studyPlan.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM StudyTask t WHERE t.studyPlan.user.id = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);

    @Query("SELECT COALESCE(SUM(t.durationMinutes), 0) FROM StudyTask t WHERE t.studyPlan.user.id = :userId AND t.status = 'COMPLETED'")
    long sumCompletedMinutesByUserId(@Param("userId") Long userId);
}
