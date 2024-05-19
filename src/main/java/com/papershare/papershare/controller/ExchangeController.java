package com.papershare.papershare.controller;

import com.papershare.papershare.model.*;
import com.papershare.papershare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
                exchangeRequests.sort(Comparator.comparing(ExchangeRequest::getUpdatedAt).reversed());

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

    @GetMapping("/dialogue")
    public String getMessageDialogue(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                   @RequestParam(name = "exchange_id") Long exchangeId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR) {
                        if(Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId()) ||
                                Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId())) {
                            model.addAttribute("user", user.get());
                            model.addAttribute("exchange_request", exchangeRequest.get());
                            return "exchange/exchange_dialogue";
                        }
                    } else {
                        return "redirect:/";
                    }
                } else {
                    return "redirect:/?error=exchangeNotFound&exchange_id=" + exchangeId;
                }
            }
        }
        return "redirect:/?error=authProblem";
    }

    @PostMapping("/dialogue")
    @ResponseBody
    public ResponseEntity<Message> postMessageDialogue(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody MessageRequest request) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(request.getExchangeRequestId());
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR) {
                        if(Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId()) ||
                                Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId())) {
                            if(request.getMessage().length() <= 1000) {
                                Message newMessage = new Message();
                                newMessage.setSender(user.get());
                                newMessage.setMessage(request.getMessage());
                                newMessage.setExchangeRequest(exchangeRequest.get());
                                exchangeRequest.get().getMessages().add(newMessage);
                                exchangeRequestService.save(exchangeRequest.get());
                                return new ResponseEntity<>(newMessage, HttpStatus.OK);
                            } else {
                                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                            }
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/dialogue/messages/{exchangeRequestId}")
    @ResponseBody
    public List<MessageDTO> getMessageHistory(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable(name = "exchangeRequestId") Long exchangeRequestId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeRequestId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR) {
                        if(Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId()) ||
                                Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId())) {
                            return exchangeRequest.get().getMessages().stream().map(message -> new MessageDTO(
                                    message.getId(),
                                    message.getMessage(),
                                    message.getSender().getId(),
                                    message.getCreatedAt()
                            )).collect(Collectors.toList());
                        }
                    }
                }
            }
        }
        return null;
    }

    @GetMapping("/select")
    public String getSelect(@AuthenticationPrincipal UserDetails userDetails, Model model,
                            @RequestParam(name = "exchange_id") Long exchangeId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CREATED) &&
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                        List<Book> selectionBooks = userAuthenticationService.findByUsername(exchangeRequest.get()
                                                                                .getInitiator().getUsername())
                                                                                .get().getOwnedBooks().stream().toList();
                        if (!selectionBooks.isEmpty()) {
                            model.addAttribute("user", user.get());
                            model.addAttribute("selection_books", selectionBooks);
                            model.addAttribute("exchange_id", exchangeId);
                            return "exchange/exchange_select";
                        }
                    }
                } else {
                    return "redirect:/?error=exchangeNotFound&exchange_id=" + exchangeId;
                }
            }
        }
        return "redirect:/?error=authProblem";
    }

    @GetMapping("/select/confirm")
    public String getSelectConfirm(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                   @RequestParam(name = "book_id", required = false) Long bookId,
                                   @RequestParam(name = "exchange_id") Long exchangeId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CREATED) &&
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                        if(bookId != null) {
                            Book selectedBook = bookService.getBookById(bookId);
                            if (selectedBook != null && Objects.equals(selectedBook.getOwner().getId(), exchangeRequest.get().getInitiator().getId())) {
                                exchangeRequest.get().setOfferedBook(selectedBook);
                                exchangeRequest.get().setStatus(ExchangeRequestStatus.ACCEPTED_BY_OWNER);
                                exchangeRequestService.save(exchangeRequest.get());
                                return "redirect:/exchange/view";
                            }
                        } else {
                            return "redirect:/?error=g";
                        }
                    } else if(exchangeRequest.get().getOfferedBook() != null  &&
                            exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.ACCEPTED_BY_OWNER) &&
                            Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId())) {
                        exchangeRequest.get().setStatus(ExchangeRequestStatus.ACCEPTED_BY_INITIATOR);
                        exchangeRequestService.save(exchangeRequest.get());
                        return "redirect:/exchange/view";
                    }
                } else {
                    return "redirect:/?error=exchangeNotFound&exchange_id=" + exchangeId;
                }
            }
        }
        return "redirect:/?error=authProblem";
    }

    @GetMapping("/cancel")
    public String postCancel(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @RequestParam(name = "exchange_id") Long exchange_id) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchange_id);
                if(exchangeRequest.isPresent()) {
                    if(Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId()) ||
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                        if(!exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CANCELLED) ||
                                !exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.SUCCESS) ||
                                !exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.FAILURE)) {
                            exchangeRequest.get().setStatus(ExchangeRequestStatus.CANCELLED);
                            exchangeRequestService.save(exchangeRequest.get());
                            return "redirect:/exchange/view";
                        }
                    }
                } else {
                    return "redirect:/?error=exchangeNotFound&exchange_id=" + exchange_id;
                }
            }
        }
        return "redirect:/?error=authProblem";
    }
}
