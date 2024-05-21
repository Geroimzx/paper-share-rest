package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.UserRating;
import com.papershare.papershare.repository.UserRatingRepository;
import com.papershare.papershare.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private UserRatingRepository userRatingRepository;

    @Autowired
    public void setUserRatingRepository(UserRatingRepository userRatingRepository) {
        this.userRatingRepository = userRatingRepository;
    }

    @Override
    public UserRating save(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }

    @Override
    public List<UserRating> getUserRatingByRateeId(Long rateeId) {
        return userRatingRepository.getUserRatingByRateeId(rateeId);
    }
}
