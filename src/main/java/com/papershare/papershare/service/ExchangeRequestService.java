package com.papershare.papershare.service;

import com.papershare.papershare.model.ExchangeRequest;

import java.util.List;
import java.util.Optional;

public interface ExchangeRequestService {
    ExchangeRequest save(ExchangeRequest exchangeRequest);

    Optional<ExchangeRequest> getExchangeRequestById(Long id);

    List<ExchangeRequest> getAllExchangeRequests();

    List<ExchangeRequest> getAllExchangeRequestsByUserId(Long userId);

    void deleteExchangeRequest(Long id);

    int getCountOfAllExchangeRequests();
}
