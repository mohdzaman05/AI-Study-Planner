package com.studyplanner.dto;

import java.util.ArrayList;
import java.util.List;

public class ProgressOverviewResponse {
    private int overallProgressPercentage;
    private long totalTopics;
    private long completedTopics;
    private long remainingTopics;
    private long totalTasks;
    private long completedTasks;
    private long missedTasks;
    private double totalStudyHoursLogged;
    private List<SubjectProgressDto> subjects = new ArrayList<>();

    public ProgressOverviewResponse() {
    }

    public int getOverallProgressPercentage() {
        return overallProgressPercentage;
    }

    public void setOverallProgressPercentage(int overallProgressPercentage) {
        this.overallProgressPercentage = overallProgressPercentage;
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

    public long getRemainingTopics() {
        return remainingTopics;
    }

    public void setRemainingTopics(long remainingTopics) {
        this.remainingTopics = remainingTopics;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getMissedTasks() {
        return missedTasks;
    }

    public void setMissedTasks(long missedTasks) {
        this.missedTasks = missedTasks;
    }

    public double getTotalStudyHoursLogged() {
        return totalStudyHoursLogged;
    }

    public void setTotalStudyHoursLogged(double totalStudyHoursLogged) {
        this.totalStudyHoursLogged = totalStudyHoursLogged;
    }

    public List<SubjectProgressDto> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectProgressDto> subjects) {
        this.subjects = subjects;
    }
}
