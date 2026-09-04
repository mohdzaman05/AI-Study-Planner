package com.studyplanner.dto;

import com.studyplanner.model.PlanStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyPlanResponse {
    private Long id;
    private String title;
    private LocalDateTime generatedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double availableHoursPerDay;
    private String preferredStartTime;
    private PlanStatus status;
    private String promptSummary;
    private int totalTasks;
    private int completedTasks;
    private int progressPercentage;
    private List<StudyTaskResponse> tasks = new ArrayList<>();

    public StudyPlanResponse() {
    }

    public StudyPlanResponse(Long id, String title, LocalDateTime generatedAt, LocalDate startDate, LocalDate endDate,
                             Double availableHoursPerDay, String preferredStartTime, PlanStatus status,
                             String promptSummary, List<StudyTaskResponse> tasks) {
        this.id = id;
        this.title = title;
        this.generatedAt = generatedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableHoursPerDay = availableHoursPerDay;
        this.preferredStartTime = preferredStartTime;
        this.status = status;
        this.promptSummary = promptSummary;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.totalTasks = this.tasks.size();
        this.completedTasks = (int) this.tasks.stream()
                .filter(t -> t.getStatus() != null && t.getStatus().name().equals("COMPLETED"))
                .count();
        this.progressPercentage = this.totalTasks > 0 ? (int) Math.round((double) this.completedTasks / this.totalTasks * 100) : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public String getPromptSummary() {
        return promptSummary;
    }

    public void setPromptSummary(String promptSummary) {
        this.promptSummary = promptSummary;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public List<StudyTaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<StudyTaskResponse> tasks) {
        this.tasks = tasks;
        if (tasks != null) {
            this.totalTasks = tasks.size();
            this.completedTasks = (int) tasks.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().name().equals("COMPLETED"))
                    .count();
            this.progressPercentage = this.totalTasks > 0 ? (int) Math.round((double) this.completedTasks / this.totalTasks * 100) : 0;
        }
    }
}
