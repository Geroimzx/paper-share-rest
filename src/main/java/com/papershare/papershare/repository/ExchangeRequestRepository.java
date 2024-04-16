package com.papershare.papershare.repository;

import com.papershare.papershare.model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

}
