package com.papershare.papershare.service;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    List<Book> getRecommendations(User user, List<Book> allBooks);
}
