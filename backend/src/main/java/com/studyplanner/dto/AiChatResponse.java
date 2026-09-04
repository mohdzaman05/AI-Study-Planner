package com.studyplanner.dto;

import java.time.LocalDateTime;

public class AiChatResponse {
    private String reply;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AiChatResponse() {
    }

    public AiChatResponse(String reply) {
        this.reply = reply;
        this.timestamp = LocalDateTime.now();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
