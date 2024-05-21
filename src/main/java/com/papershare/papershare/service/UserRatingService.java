package com.papershare.papershare.service;

import com.papershare.papershare.model.UserRating;

import java.util.List;

public interface UserRatingService {
    UserRating save(UserRating userRating);

    List<UserRating> getUserRatingByRateeId(Long rateeId);
}
