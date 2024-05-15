package com.papershare.papershare.repository;

import com.papershare.papershare.model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    @Query("SELECT er FROM ExchangeRequest er " +
            "LEFT JOIN er.offeredBook bo ON bo.owner.id = :userId " +
            "LEFT JOIN er.requestedBook br ON br.owner.id = :userId " +
            "WHERE (bo.id IS NOT NULL OR br.id IS NOT NULL) OR er.initiator.id = :userId")
    List<ExchangeRequest> getAllExchangeRequestsByUserId(@Param("userId") Long userId);
}