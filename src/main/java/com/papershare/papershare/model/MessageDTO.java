package com.papershare.papershare.model;

import java.time.Instant;

public class MessageDTO {
    private Long id;
    private String message;
    private Long senderId;
    private Instant createdAt;

    public MessageDTO() {
    }

    public MessageDTO(Long id, String message, Long senderId, Instant createdAt) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}