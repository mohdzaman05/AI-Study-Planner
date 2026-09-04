package com.studyplanner.dto;

import com.studyplanner.model.Difficulty;
import com.studyplanner.model.TopicStatus;
import java.time.LocalDateTime;

public class TopicResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private String name;
    private String description;
    private Difficulty difficulty;
    private Integer estimatedMinutes;
    private TopicStatus status;
    private LocalDateTime createdAt;

    public TopicResponse() {
    }

    public TopicResponse(Long id, Long subjectId, String subjectName, String name, String description,
                         Difficulty difficulty, Integer estimatedMinutes, TopicStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.estimatedMinutes = estimatedMinutes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public TopicStatus getStatus() {
        return status;
    }

    public void setStatus(TopicStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
