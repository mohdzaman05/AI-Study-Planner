package com.studyplanner.dto;

import com.studyplanner.model.Difficulty;
import com.studyplanner.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class SubjectRequest {

    @NotBlank(message = "Subject name is required")
    @Size(min = 2, max = 120, message = "Subject name must be between 2 and 120 characters")
    private String name;

    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty = Difficulty.MEDIUM;

    @NotNull(message = "Priority is required")
    private Priority priority = Priority.MEDIUM;

    private LocalDate examDate;

    public SubjectRequest() {
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
}
