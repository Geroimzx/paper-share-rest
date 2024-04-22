package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping()
public class HomeController {

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        user.ifPresent(value -> model.addAttribute("user", value));

        // Отримання списку книг з бази даних
        List<Book> books = bookService.getAllBooks();

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("books", books);

        return "index";
    }
}
