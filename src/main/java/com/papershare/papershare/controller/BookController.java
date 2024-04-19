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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

        return "book/books";
    }

    @GetMapping("/view/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("book", book);

        return "book/book_view";
    }

    @GetMapping("/create")
    public String getCreate(Model model, @ModelAttribute("book") Book book) {

        return "book/book_create";
    }

    @PostMapping
    public String postCreate(Model model, @ModelAttribute("book") Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        if(user.isPresent()) {

            book.setAvailable(true);

            book.setOwner(user.get());

            Book createdBook = bookService.createBook(book);

            if(createdBook != null) {
                Long id = createdBook.getId();

                return "redirect:/book/view/{id}";
            }
            return "redirect:/book/create?error=\"create_problem\"";
        }
        return "redirect:/book/create?error=\"auth_problem\"";
    }
}
