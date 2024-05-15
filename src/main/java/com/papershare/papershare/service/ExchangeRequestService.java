package com.papershare.papershare.service;

import com.papershare.papershare.model.ExchangeRequest;

import java.util.List;

public interface ExchangeRequestService {
    ExchangeRequest createExchangeRequest(ExchangeRequest exchangeRequest);

    ExchangeRequest getExchangeRequestById(Long id);

    List<ExchangeRequest> getAllExchangeRequests();

    List<ExchangeRequest> getAllExchangeRequestsByUserId(Long userId);

    ExchangeRequest updateExchangeRequest(Long id, ExchangeRequest exchangeRequest);

    void deleteExchangeRequest(Long id);

    int getCountOfAllExchangeRequests();
}
