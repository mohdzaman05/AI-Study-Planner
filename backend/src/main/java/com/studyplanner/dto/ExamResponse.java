package com.studyplanner.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExamResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private LocalDate examDate;
    private String examTime;
    private String importantTopics;
    private String notes;
    private long daysRemaining;
    private LocalDateTime createdAt;

    public ExamResponse() {
    }

    public ExamResponse(Long id, Long subjectId, String subjectName, LocalDate examDate,
                        String examTime, String importantTopics, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.examDate = examDate;
        this.examTime = examTime;
        this.importantTopics = importantTopics;
        this.notes = notes;
        this.createdAt = createdAt;
        if (examDate != null) {
            this.daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), examDate);
        }
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

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
        if (examDate != null) {
            this.daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), examDate);
        }
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getImportantTopics() {
        return importantTopics;
    }

    public void setImportantTopics(String importantTopics) {
        this.importantTopics = importantTopics;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
