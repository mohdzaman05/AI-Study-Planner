package com.studyplanner.dto;

import com.studyplanner.model.Difficulty;
import com.studyplanner.model.TopicStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TopicRequest {

    @NotBlank(message = "Topic name is required")
    @Size(min = 2, max = 150, message = "Topic name must be between 2 and 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty = Difficulty.MEDIUM;

    @NotNull(message = "Estimated study time is required")
    @Min(value = 10, message = "Estimated study time must be at least 10 minutes")
    @Max(value = 360, message = "Estimated study time cannot exceed 360 minutes")
    private Integer estimatedMinutes = 45;

    private TopicStatus status = TopicStatus.NOT_STARTED;

    public TopicRequest() {
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
}
