package com.studyplanner.dto;

public class AdjustPlanRequest {
    private Long missedTaskId;
    private String reason;

    public AdjustPlanRequest() {
    }

    public AdjustPlanRequest(Long missedTaskId, String reason) {
        this.missedTaskId = missedTaskId;
        this.reason = reason;
    }

    public Long getMissedTaskId() {
        return missedTaskId;
    }

    public void setMissedTaskId(Long missedTaskId) {
        this.missedTaskId = missedTaskId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
