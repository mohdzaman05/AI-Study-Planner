package com.studyplanner.dto;

import com.studyplanner.model.Difficulty;
import com.studyplanner.model.Priority;
import java.time.LocalDate;

public class SubjectProgressDto {
    private Long subjectId;
    private String subjectName;
    private Difficulty difficulty;
    private Priority priority;
    private LocalDate examDate;
    private long totalTopics;
    private long completedTopics;
    private int progressPercentage;

    public SubjectProgressDto() {
    }

    public SubjectProgressDto(Long subjectId, String subjectName, Difficulty difficulty, Priority priority,
                              LocalDate examDate, long totalTopics, long completedTopics, int progressPercentage) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.difficulty = difficulty;
        this.priority = priority;
        this.examDate = examDate;
        this.totalTopics = totalTopics;
        this.completedTopics = completedTopics;
        this.progressPercentage = progressPercentage;
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

    public long getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(long totalTopics) {
        this.totalTopics = totalTopics;
    }

    public long getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(long completedTopics) {
        this.completedTopics = completedTopics;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
