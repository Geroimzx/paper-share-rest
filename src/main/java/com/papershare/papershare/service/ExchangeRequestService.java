package com.papershare.papershare.service;

import com.papershare.papershare.model.ExchangeRequest;

import java.util.List;

public interface ExchangeRequestService {
    ExchangeRequest save(ExchangeRequest exchangeRequest);

    ExchangeRequest getExchangeRequestById(Long id);

    List<ExchangeRequest> getAllExchangeRequests();

    List<ExchangeRequest> getAllExchangeRequestsByUserId(Long userId);

    void deleteExchangeRequest(Long id);

    int getCountOfAllExchangeRequests();
}
