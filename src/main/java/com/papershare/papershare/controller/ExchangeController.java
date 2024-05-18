package com.papershare.papershare.controller;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.ExchangeRequest;
import com.papershare.papershare.model.ExchangeRequestStatus;
import com.papershare.papershare.model.User;
import com.papershare.papershare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

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

    @GetMapping("/view")
    public String getExchangeById(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                List<ExchangeRequest> exchangeRequests = exchangeRequestService.getAllExchangeRequestsByUserId(user.get().getId());

                if(!exchangeRequests.isEmpty()) {
                    // Передача обмінів на сторінку через Thymeleaf
                    model.addAttribute("exchange_requests", exchangeRequests);
                }
                return "exchange/exchange_view";
            }
        }
        return "redirect:/?error=authError";
    }

    @GetMapping("/create")
    public String getCreateRequest(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                   @RequestParam(name = "requestedBook") Long requestedBookId,
                                   @ModelAttribute("requested_book") Book book) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                Book requestedBook = bookService.getBookById(requestedBookId);

                if(requestedBook != null) {
                    // Перевірка, чи книга належить користувачу
                    if (requestedBook.getOwner().getId().equals(user.get().getId())) {
                        return "redirect:/?error=ownBookRequest";
                    } else {
                        model.addAttribute("requested_book", requestedBook);
                        return "exchange/exchange_create";
                    }
                } else {
                    return "redirect:/?error=bookNotFound&book_id=" + requestedBookId;
                }
            }
        }
        return "redirect:/book/view/" + requestedBookId +"?error=authProblem";
    }

    @PostMapping("/create")
    public String postCreateRequest(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                    @RequestParam(name = "requestedBook") Long requestedBookId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                Book requestedBook = bookService.getBookById(requestedBookId);

                if(requestedBook != null) {
                    // Перевірка, чи книга належить користувачу
                    if (requestedBook.getOwner().getId().equals(user.get().getId())) {
                        return "redirect:/?error=ownBookRequest";
                    } else {
                        ExchangeRequest exchangeRequest = new ExchangeRequest();
                        exchangeRequest.setRequestedBook(requestedBook);
                        exchangeRequest.setInitiator(user.get());
                        exchangeRequest.setStatus(ExchangeRequestStatus.CREATED);

                        ExchangeRequest createdExchangeRequest = exchangeRequestService.save(exchangeRequest);

                        return "redirect:/exchange/view";
                    }
                } else {
                    return "redirect:/?error=bookNotFound&book_id=" + requestedBookId;
                }
            }
        }
        return "redirect:/book/view/" + requestedBookId +"?error=authProblem";
    }

    @GetMapping("/select")
    public String getSelect(@AuthenticationPrincipal UserDetails userDetails, Model model,
                            @RequestParam(name = "exchange_id") Long exchange_id) {
        return "";
    }

    @PostMapping("/select")
    public String postSelect() {
        return "";
    }

    @GetMapping("/cancel")
    public String postCancel(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @RequestParam(name = "exchange_id") Long exchange_id) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                ExchangeRequest exchangeRequest = exchangeRequestService.getExchangeRequestById(exchange_id);
                if(exchangeRequest != null) {
                    if(Objects.equals(exchangeRequest.getInitiator().getId(), user.get().getId()) || Objects.equals(exchangeRequest.getRequestedBook().getOwner().getId(), user.get().getId())) {
                        exchangeRequest.setStatus(ExchangeRequestStatus.CANCELLED);
                        exchangeRequestService.save(exchangeRequest);
                        return "redirect:/exchange/view";
                    }
                } else {
                    return "redirect:/?error=exchangeNotFound&exchange_id=" + exchange_id;
                }
            }
        }
        return "redirect:/?error=authProblem";
    }
}
