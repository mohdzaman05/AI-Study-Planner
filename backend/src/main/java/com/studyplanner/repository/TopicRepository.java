package com.studyplanner.repository;

import com.studyplanner.model.Topic;
import com.studyplanner.model.TopicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findBySubjectIdOrderByIdAsc(Long subjectId);

    @Query("SELECT t FROM Topic t WHERE t.id = :topicId AND t.subject.user.id = :userId")
    Optional<Topic> findByIdAndUserId(@Param("topicId") Long topicId, @Param("userId") Long userId);

    @Query("SELECT t FROM Topic t WHERE t.subject.user.id = :userId")
    List<Topic> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.subject.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.subject.user.id = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TopicStatus status);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.subject.id = :subjectId")
    long countBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.subject.id = :subjectId AND t.status = :status")
    long countBySubjectIdAndStatus(@Param("subjectId") Long subjectId, @Param("status") TopicStatus status);
}
