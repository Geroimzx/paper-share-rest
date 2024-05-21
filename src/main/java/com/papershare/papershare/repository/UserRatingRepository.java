package com.papershare.papershare.repository;

import com.papershare.papershare.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> getUserRatingByRateeId(Long userId);
}
