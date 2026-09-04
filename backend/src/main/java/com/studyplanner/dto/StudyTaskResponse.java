package com.studyplanner.dto;

import com.studyplanner.model.Priority;
import com.studyplanner.model.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudyTaskResponse {
    private Long id;
    private Long studyPlanId;
    private Long subjectId;
    private String subjectName;
    private Long topicId;
    private String topicName;
    private LocalDate taskDate;
    private String startTime;
    private Integer durationMinutes;
    private Priority priority;
    private String reasonRecommendation;
    private TaskStatus status;
    private LocalDateTime completedAt;

    public StudyTaskResponse() {
    }

    public StudyTaskResponse(Long id, Long studyPlanId, Long subjectId, String subjectName, Long topicId,
                             String topicName, LocalDate taskDate, String startTime, Integer durationMinutes,
                             Priority priority, String reasonRecommendation, TaskStatus status, LocalDateTime completedAt) {
        this.id = id;
        this.studyPlanId = studyPlanId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.topicId = topicId;
        this.topicName = topicName;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.priority = priority;
        this.reasonRecommendation = reasonRecommendation;
        this.status = status;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudyPlanId() {
        return studyPlanId;
    }

    public void setStudyPlanId(Long studyPlanId) {
        this.studyPlanId = studyPlanId;
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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getReasonRecommendation() {
        return reasonRecommendation;
    }

    public void setReasonRecommendation(String reasonRecommendation) {
        this.reasonRecommendation = reasonRecommendation;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
