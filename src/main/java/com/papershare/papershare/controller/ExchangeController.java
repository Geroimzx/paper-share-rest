package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.ImageUploadService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

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

    @GetMapping("/create")
    public String getCreateRequest(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model, @RequestParam(name = "requestedBook") Long requestedBookId, @ModelAttribute("requested_book") Book book) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                Book requestedBook = bookService.getBookById(requestedBookId);

                if(requestedBook != null) {
                    model.addAttribute("requested_book", requestedBook);

                    return "exchange/exchange_create";
                }
                return "redirect:/?error=bookNotFound&book_id=" + requestedBookId;
            }
        }
        return "redirect:/book/view/" + requestedBookId +"?error=authProblem";
    }
}
