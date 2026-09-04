package com.studyplanner.dto;

import com.studyplanner.model.Difficulty;
import com.studyplanner.model.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubjectResponse {
    private Long id;
    private String name;
    private String description;
    private Difficulty difficulty;
    private Priority priority;
    private LocalDate examDate;
    private long topicCount;
    private long completedTopicCount;
    private int progressPercentage;
    private LocalDateTime createdAt;

    public SubjectResponse() {
    }

    public SubjectResponse(Long id, String name, String description, Difficulty difficulty, Priority priority,
                           LocalDate examDate, long topicCount, long completedTopicCount, int progressPercentage,
                           LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.priority = priority;
        this.examDate = examDate;
        this.topicCount = topicCount;
        this.completedTopicCount = completedTopicCount;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public long getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(long topicCount) {
        this.topicCount = topicCount;
    }

    public long getCompletedTopicCount() {
        return completedTopicCount;
    }

    public void setCompletedTopicCount(long completedTopicCount) {
        this.completedTopicCount = completedTopicCount;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
