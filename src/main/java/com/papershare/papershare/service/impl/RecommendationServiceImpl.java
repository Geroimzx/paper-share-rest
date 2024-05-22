package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.ExchangeRequest;
import com.papershare.papershare.model.User;
import com.papershare.papershare.model.WishlistItem;
import com.papershare.papershare.service.RecommendationService;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private double calculateBookSimilarity(Book book1, Book book2) {
        String text1 = String.join(" ",
                book1.getTitle(), book1.getAuthor(), book1.getPublisher(),
                book1.getLanguage(), String.valueOf(book1.getPublicationYear()),
                String.valueOf(book1.getNumberOfPages()), book1.getGenre(), book1.getDescription()
        );
        String text2 = String.join(" ",
                book2.getTitle(), book2.getAuthor(), book2.getPublisher(),
                book2.getLanguage(), String.valueOf(book2.getPublicationYear()),
                String.valueOf(book2.getNumberOfPages()), book2.getGenre(), book2.getDescription()
        );

        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        return cosineSimilarity.cosineSimilarity(getTermFrequencies(text1), getTermFrequencies(text2));
    }

    private Map<CharSequence, Integer> getTermFrequencies(String text) {
        Map<CharSequence, Integer> termFrequencies = new HashMap<>();
        for (String term : text.toLowerCase().split("\\s+")) { // Split by whitespace
            termFrequencies.put(term, termFrequencies.getOrDefault(term, 0) + 1);
        }
        return termFrequencies;
    }

    @Override
    public List<Book> getRecommendations(User user, List<Book> allBooks) {
        List<Book> likedBooks = new ArrayList<>();

        likedBooks.addAll(user.getOwnedBooks());
        likedBooks.addAll(user.getExchangeRequests().stream().map(ExchangeRequest::getRequestedBook).toList());
        likedBooks.addAll(user.getWishlistItems().stream().map(WishlistItem::getBook).toList());

        Map<Book, Double> similarityScores = new HashMap<>();
        for (Book likedBook : likedBooks) {
            for (Book book : allBooks) {
                if (!likedBook.equals(book)) {
                    double similarity = calculateBookSimilarity(likedBook, book);
                    similarityScores.put(book, similarity);
                }
            }
        }

        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(8)
                .collect(Collectors.toList());
    }
}
