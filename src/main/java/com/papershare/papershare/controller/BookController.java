package com.papershare.papershare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/book")
public class BookController {

    @GetMapping("/view/all")
    public String getAllBooks() {

        return "books";
    }

    @GetMapping("/view/{id}")
    public String getBook(@PathVariable Long id) {
        return "book_view";
    }
}
