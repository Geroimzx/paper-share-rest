package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.RecommendationService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping()
public class HomeController {

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    private RecommendationService recommendationService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setRecommendationService(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                List<Book> recommendedBooks = recommendationService.getRecommendations(
                        user.get(),
                        bookService.getAllBooks().stream().filter(
                                book -> !book
                                        .getOwner()
                                        .getId()
                                        .equals(user.get().getId()) && book.isAvailable()
                                )
                                .collect(Collectors.toList())
                );

                model.addAttribute("recommended_books", recommendedBooks);
            }
        }

        List<Book> books = bookService.getAllBooks().stream().filter(Book::isAvailable)
                .sorted(
                        Comparator.comparing(Book::getCreatedAt).reversed()
                ).collect(Collectors.toList());

        books = books.stream().limit(4).collect(Collectors.toList());

        model.addAttribute("books", books);

        return "index";
    }
}
