package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping()
public class HomeController {

    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String index(Model model) {
        // Отримання списку книг з бази даних
        List<Book> books = bookService.getAllBooks();

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("books", books);

        return "index";
    }
}
