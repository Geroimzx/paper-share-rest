package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/book")
public class BookController {

    private final String defaultImageCoverUrl = "https://storage.googleapis.com/paper-share-images/default_book_cover.png";

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    private ImageUploadService imageUploadService;

    private WishlistItemService wishlistItemService;

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

    @Autowired
    public void setWishlistItemService(WishlistItemService wishlistItemService) {
        this.wishlistItemService = wishlistItemService;
    }

    @GetMapping("/view/all")
    public String getAllBooks(@AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              @RequestParam(required = false) String sortType,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) String author,
                              @RequestParam(required = false) String publisher,
                              @RequestParam(required = false) String genre,
                              @RequestParam(required = false) String coverType,
                              @RequestParam(required = false) String language,
                              @RequestParam(required = false) Integer publicationYear,
                              @RequestParam(required = false) String isbn) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));
        }

        List<Book> books = bookService.getAllBooks().stream().filter(Book::isAvailable)
                .collect(Collectors.toList());

        if(title != null) {
            model.addAttribute("searched_title", title);
            books = books.stream().filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(author != null) {
            model.addAttribute("searched_author", author);
            books = books.stream().filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(publisher != null) {
            model.addAttribute("searched_publisher", publisher);
            books = books.stream().filter(book -> book.getPublisher().toLowerCase().contains(publisher.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(genre != null) {
            model.addAttribute("searched_genre", genre);
            books = books.stream().filter(book -> book.getGenre().toLowerCase().contains(genre.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(coverType != null) {
            model.addAttribute("searched_coverType", coverType);
            books = books.stream().filter(book -> book.getCoverType().toLowerCase().contains(coverType.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(language != null) {
            model.addAttribute("searched_language", language);
            books = books.stream().filter(book -> book.getLanguage().toLowerCase().contains(language.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if(publicationYear != null) {
            model.addAttribute("searched_publicationYear", publicationYear);
            books = books.stream().filter(book -> book.getPublicationYear().equals(publicationYear))
                    .collect(Collectors.toList());
        }
        if(isbn != null) {
            model.addAttribute("searched_isbn", isbn);
            books = books.stream().filter(book -> book.getIsbn().toLowerCase().contains(isbn.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if(sortType != null) {
            switch (sortType) {
                case "newest":
                    books.sort(Comparator.comparing(Book::getCreatedAt).reversed());
                    break;
                case "oldest":
                    books.sort(Comparator.comparing(Book::getCreatedAt));
                    break;
            }
        } else {
            books.sort(Comparator.comparing(Book::getCreatedAt).reversed());
        }

        model.addAttribute("books", books);

        return "book/books";
    }

    @GetMapping("/view/{id}")
    public String getBook(@AuthenticationPrincipal UserDetails userDetails,
                          @PathVariable Long id, Model model) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());
                boolean isInWishlist = wishlistItemService.existsByUserIdAndBookId(user.get().getId(), id);
                model.addAttribute("isInWishlist", isInWishlist);
            }
        }
        Book book = bookService.getBookById(id);

        if(book != null && book.isAvailable()) {
            model.addAttribute("book", book);

            boolean isOwner = userDetails != null && book.getOwner().getUsername().equals(userDetails.getUsername());
            model.addAttribute("isNotOwner", !isOwner);

            Optional<User> userByUsername = userAuthenticationService.findByUsername(book.getOwner().getUsername());
            if(userByUsername.isPresent()) {
                model.addAttribute("owner", userByUsername.get());

                return "book/book_view";
            }
        }
        return "error/404";
    }

    @GetMapping("/my")
    public String getMyBooks(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));

            if(user.isPresent()) {
                List<Book> books = (List<Book>) user.get().getOwnedBooks().stream().filter(Book::isAvailable).collect(Collectors.toList());

                model.addAttribute("books", books);

                return "book/book_user_menu";
            }
        }
        return "error/401";
    }

    @GetMapping("/create")
    public String getCreate(@AuthenticationPrincipal UserDetails userDetails,
                            Model model, @ModelAttribute("book") Book book) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));

            return "book/book_create";
        }
        return "error/401";
    }

    @PostMapping("/create")
    public String postCreate(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @ModelAttribute("book") Book book,
                             @RequestParam("uploaded-image") MultipartFile uploadedImage) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));

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

                    Book createdBook = bookService.save(book);

                    if(createdBook != null) {
                        Long id = createdBook.getId();

                        return "redirect:/book/view/" + id;
                    }
                }
                return "error/400";
            }
        }
        return "error/401";
    }

    @GetMapping("/edit/{id}")
    public String getEdit(@AuthenticationPrincipal UserDetails userDetails,
                            Model model, @PathVariable Long id,
                          @ModelAttribute("book") Book book) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                if(bookService.getBookById(id) != null) {
                    if(bookService.getBookById(book.getId()).isAvailable() == true) {
                        if (Objects.equals(user.get().getId(), bookService.getBookById(id).getOwner().getId())) {
                            model.addAttribute("user", user.get());

                            book = bookService.getBookById(id);

                            model.addAttribute("book", book);

                            return "book/book_edit";
                        }
                    }
                }
                return "error/400";
            }
        }
        return "error/401";
    }

    @PostMapping("/edit")
    public String postEdit(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @ModelAttribute("book") Book book,
                             @RequestParam("uploaded-image") MultipartFile uploadedImage) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                if(bookService.getBookById(book.getId()) != null) {
                    if (Objects.equals(user.get().getId(), bookService.getBookById(book.getId()).getOwner().getId())) {
                        if(bookService.getBookById(book.getId()).isAvailable() == true) {
                            model.addAttribute("user", user.get());

                            book.setAvailable(true);
                            book.setOwner(user.get());

                            if (!book.getTitle().isBlank() && !book.getAuthor().isBlank()
                                    && !book.getPublisher().isBlank() && !book.getLanguage().isBlank()
                                    && !book.getCoverType().isBlank() && book.getPublicationYear() > 0
                                    && book.getNumberOfPages() > 0 && book.getGenre() != null
                                    && !book.getGenre().isBlank() && !book.getIsbn().isBlank()) {

                                if (!uploadedImage.isEmpty() && Objects.requireNonNull(uploadedImage.getContentType()).startsWith("image/")) {
                                    String imageUrl = imageUploadService.uploadImage(uploadedImage);

                                    book.setImageUrl(imageUrl);
                                } else {
                                    book.setImageUrl(bookService.getBookById(book.getId()).getImageUrl());
                                }

                                Book createdBook = bookService.save(book);

                                if (createdBook != null) {
                                    Long id = createdBook.getId();

                                    return "redirect:/book/view/" + id;
                                }
                            }
                        }
                    }
                }
                return "error/400";
            }
        }
        return "error/401";
    }

    @GetMapping("/delete/{id}")
    public String getDelete(@AuthenticationPrincipal UserDetails userDetails,
                          Model model, @PathVariable Long id,
                          @ModelAttribute("book") Book book) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            user.ifPresent(value -> model.addAttribute("user", value));

            if(user.isPresent()) {
                if(bookService.getBookById(id) != null) {
                    if (Objects.equals(user.get().getId(), bookService.getBookById(id).getOwner().getId())) {
                        Book deletedBook = bookService.getBookById(id);
                        deletedBook.setAvailable(false);
                        bookService.save(deletedBook);

                        return "redirect:/book/my";
                    }
                }
                return "error/400";
            }
        }
        return "error/401";
    }
}
