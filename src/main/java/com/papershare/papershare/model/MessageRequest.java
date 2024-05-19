package com.papershare.papershare.model;

public class MessageRequest {
    private Long exchangeRequestId;
    private String message;

    public MessageRequest() {
    }

    public MessageRequest(Long exchangeRequestId, String message) {
        this.exchangeRequestId = exchangeRequestId;
        this.message = message;
    }

    public Long getExchangeRequestId() {
        return exchangeRequestId;
    }

    public void setExchangeRequestId(Long exchangeRequestId) {
        this.exchangeRequestId = exchangeRequestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}