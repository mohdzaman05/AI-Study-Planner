package com.studyplanner.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Daily study hours is required")
    @DecimalMin(value = "0.5", message = "Daily study hours must be at least 0.5")
    @DecimalMax(value = "16.0", message = "Daily study hours cannot exceed 16")
    private Double dailyStudyHours;

    @NotBlank(message = "Preferred study start time is required")
    private String preferredStudyTime;

    public UpdateProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getDailyStudyHours() {
        return dailyStudyHours;
    }

    public void setDailyStudyHours(Double dailyStudyHours) {
        this.dailyStudyHours = dailyStudyHours;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }
}
