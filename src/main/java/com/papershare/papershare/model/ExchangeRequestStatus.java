package com.papershare.papershare.model;

public enum ExchangeRequestStatus {
    CREATED,
    ACCEPTED_BY_OWNER,
    DECLINED_BY_OWNER,
    ACCEPTED_BY_INITIATOR,
    DECLINED_BY_INITIATOR,
    CANCELLED,
    DECLINED,
    PENDING,
    SUCCESS
}