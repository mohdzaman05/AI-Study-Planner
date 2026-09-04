package com.studyplanner.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AiChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private Long subjectContextId;

    public AiChatRequest() {
    }

    public AiChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSubjectContextId() {
        return subjectContextId;
    }

    public void setSubjectContextId(Long subjectContextId) {
        this.subjectContextId = subjectContextId;
    }
}
