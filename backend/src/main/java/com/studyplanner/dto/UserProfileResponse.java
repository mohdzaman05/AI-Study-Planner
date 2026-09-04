package com.studyplanner.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private Double dailyStudyHours;
    private String preferredStudyTime;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String fullName, String email, Double dailyStudyHours, String preferredStudyTime, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.dailyStudyHours = dailyStudyHours;
        this.preferredStudyTime = preferredStudyTime;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
