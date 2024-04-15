package com.papershare.papershare.repository;

import com.papershare.papershare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);
}
