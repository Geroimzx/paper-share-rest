package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {

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

    @GetMapping("/view/all")
    public String getAllBooks(Model model) {
        // Отримання списку книг з бази даних
        List<Book> books = bookService.getAllBooks();

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("books", books);

        return "books";
    }

    @GetMapping("/view/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("book", book);

        return "book_view";
    }

    @GetMapping("/create")
    public String getCreate(Model model) {

        return "book_create";
    }
}
