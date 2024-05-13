package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.ExchangeRequest;
import com.papershare.papershare.repository.ExchangeRequestRepository;
import com.papershare.papershare.service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRequestServiceImpl implements ExchangeRequestService {

    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    public ExchangeRequest createExchangeRequest(ExchangeRequest exchangeRequest) {
        return exchangeRequestRepository.save(exchangeRequest);
    }

    public ExchangeRequest getExchangeRequestById(Long id) {
        return exchangeRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Exchange request not found"));
    }

    public List<ExchangeRequest> getAllExchangeRequests() {
        return exchangeRequestRepository.findAll();
    }

    public ExchangeRequest updateExchangeRequest(Long id, ExchangeRequest exchangeRequest) {
        ExchangeRequest existingRequest = exchangeRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Exchange request not found"));
        existingRequest.setInitiator(exchangeRequest.getInitiator());
        existingRequest.setOfferedBook(exchangeRequest.getOfferedBook());
        existingRequest.setRequestedBook(exchangeRequest.getRequestedBook());
        existingRequest.setStatus(exchangeRequest.getStatus());
        return exchangeRequestRepository.save(existingRequest);
    }

    public void deleteExchangeRequest(Long id) {
        exchangeRequestRepository.deleteById(id);
    }

    @Override
    public int getCountOfAllExchangeRequests() {
        return exchangeRequestRepository.findAll().size();
    }
}