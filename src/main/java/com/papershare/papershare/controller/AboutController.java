package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.ExchangeRequestService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/about")
public class AboutController {

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    private ExchangeRequestService exchangeRequestService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setExchangeRequestService(ExchangeRequestService exchangeRequestService) {
        this.exchangeRequestService = exchangeRequestService;
    }

    @GetMapping()
    public String about(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));
        }

        long countOfAvailableBooks = bookService.getCountOfAllAvailableBooks();

        long countOfExchangeRequest = exchangeRequestService.getCountOfAllSuccessExchangeRequests();

        long countOfUsers = userAuthenticationService.getCountOfAllUsers();

        model.addAttribute("countOfAvailableBooks", countOfAvailableBooks);

        model.addAttribute("countOfExchangeRequest", countOfExchangeRequest);

        model.addAttribute("countOfUsers", countOfUsers);

        return "about";
    }

}
