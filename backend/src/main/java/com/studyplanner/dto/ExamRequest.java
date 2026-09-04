package com.studyplanner.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ExamRequest {

    @NotNull(message = "Subject is required")
    private Long subjectId;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;

    private String examTime;
    private String importantTopics;
    private String notes;

    public ExamRequest() {
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
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
}
