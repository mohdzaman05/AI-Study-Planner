package com.studyplanner.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GeneratePlanRequest {

    @NotNull(message = "Daily study hours is required")
    @DecimalMin(value = "0.5", message = "Daily study hours must be at least 0.5")
    @DecimalMax(value = "14.0", message = "Daily study hours cannot exceed 14")
    private Double availableHoursPerDay = 3.0;

    private String preferredStartTime = "18:00";

    private Integer planDurationDays = 14;

    private List<String> preferredStudyDays;

    private String personalGoals;

    public GeneratePlanRequest() {
    }

    public Double getAvailableHoursPerDay() {
        return availableHoursPerDay;
    }

    public void setAvailableHoursPerDay(Double availableHoursPerDay) {
        this.availableHoursPerDay = availableHoursPerDay;
    }

    public String getPreferredStartTime() {
        return preferredStartTime;
    }

    public void setPreferredStartTime(String preferredStartTime) {
        this.preferredStartTime = preferredStartTime;
    }

    public Integer getPlanDurationDays() {
        return planDurationDays;
    }

    public void setPlanDurationDays(Integer planDurationDays) {
        this.planDurationDays = planDurationDays;
    }

    public List<String> getPreferredStudyDays() {
        return preferredStudyDays;
    }

    public void setPreferredStudyDays(List<String> preferredStudyDays) {
        this.preferredStudyDays = preferredStudyDays;
    }

    public String getPersonalGoals() {
        return personalGoals;
    }

    public void setPersonalGoals(String personalGoals) {
        this.personalGoals = personalGoals;
    }
}
