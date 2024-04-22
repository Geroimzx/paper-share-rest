package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.ImageUploadService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/book")
public class BookController {

    private final String defaultImageCoverUrl = "https://storage.googleapis.com/paper-share-images/default_book_cover.png";

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    private ImageUploadService imageUploadService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setImageUploadService(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @GetMapping("/view/all")
    public String getAllBooks(Model model) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        user.ifPresent(value -> model.addAttribute("user", value));

        // Отримання списку книг з бази даних
        List<Book> books = bookService.getAllBooks();

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("books", books);

        return "book/books";
    }

    @GetMapping("/view/{id}")
    public String getBook(@PathVariable Long id, Model model) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        user.ifPresent(value -> model.addAttribute("user", value));

        Book book = bookService.getBookById(id);

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("book", book);

        return "book/book_view";
    }

    @GetMapping("/create")
    public String getCreate(Model model, @ModelAttribute("book") Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        user.ifPresent(value -> model.addAttribute("user", value));

        return "book/book_create";
    }

    @PostMapping("/create")
    public String postCreate(Model model, @ModelAttribute("book") Book book, @RequestParam("uploaded-image") MultipartFile uploadedImage) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> user = userAuthenticationService.findByUsername(authentication.getName());

        if(user.isPresent()) {
            book.setAvailable(true);

            book.setOwner(user.get());

            if(!book.getTitle().isBlank() && !book.getAuthor().isBlank()
                    && !book.getPublisher().isBlank() && !book.getLanguage().isBlank()
                    && !book.getCoverType().isBlank() &&  book.getPublicationYear() > 0
                    &&  book.getNumberOfPages() > 0 && book.getGenre() != null
                    && !book.getGenre().isBlank() && !book.getIsbn().isBlank()) {

                if(!uploadedImage.isEmpty() && Objects.requireNonNull(uploadedImage.getContentType()).startsWith("image/")) {
                    String imageUrl = imageUploadService.uploadImage(uploadedImage);

                    book.setImageUrl(imageUrl);
                }

                if(uploadedImage.isEmpty()) {
                    book.setImageUrl(defaultImageCoverUrl);
                }

                Book createdBook = bookService.createBook(book);

                if(createdBook != null) {
                    Long id = createdBook.getId();

                    return "redirect:/book/view/" + id;
                }
            }
            return "redirect:/book/create?error=\"create_problem\"";
        }
        return "redirect:/book/create?error=\"auth_problem\"";
    }
}
