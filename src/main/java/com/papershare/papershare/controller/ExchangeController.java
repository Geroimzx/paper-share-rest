package com.papershare.papershare.controller;

import com.papershare.papershare.model.*;
import com.papershare.papershare.service.*;
import org.apache.tomcat.util.http.parser.Ranges;
import org.hibernate.validator.cfg.defs.RangeDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ranges.Range;

import java.awt.font.NumericShaper;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

    private ExchangeRequestService exchangeRequestService;

    private UserRatingService userRatingService;

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

    @Autowired
    public void setUserRatingService(UserRatingService userRatingService) {
        this.userRatingService = userRatingService;
    }

    @GetMapping("/view")
    public String getExchangeById(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) throws IOException {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                List<ExchangeRequest> exchangeRequests = exchangeRequestService.getAllExchangeRequestsByUserId(user.get().getId());
                exchangeRequests.sort(Comparator.comparing(ExchangeRequest::getUpdatedAt).reversed());

                if(!exchangeRequests.isEmpty()) {
                    model.addAttribute("exchange_requests", exchangeRequests);
                }
                return "exchange/exchange_view";
            }
        }
        return "error/401";
    }

    @GetMapping("/create")
    public String getCreateRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "requestedBook") Long requestedBookId,
            @ModelAttribute("requested_book") Book book
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                model.addAttribute("user", user.get());

                Book requestedBook = bookService.getBookById(requestedBookId);

                if(requestedBook != null && requestedBook.isAvailable()) {
                    if (requestedBook.getOwner().getId().equals(user.get().getId())) {
                        return "error/400";
                    } else {
                        model.addAttribute("requested_book", requestedBook);
                        return "exchange/exchange_create";
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @PostMapping("/create")
    public String postCreateRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "requestedBook") Long requestedBookId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                Book requestedBook = bookService.getBookById(requestedBookId);

                if(requestedBook != null && requestedBook.isAvailable()) {
                    if (requestedBook.getOwner().getId().equals(user.get().getId())) {
                        return "error/400";
                    } else {
                        ExchangeRequest exchangeRequest = new ExchangeRequest();
                        exchangeRequest.setRequestedBook(requestedBook);
                        exchangeRequest.setInitiator(user.get());
                        exchangeRequest.setStatus(ExchangeRequestStatus.CREATED);
                        exchangeRequestService.save(exchangeRequest);

                        return "redirect:/exchange/view";
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @GetMapping("/dialogue")
    public String getMessageDialogue(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchangeId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getOfferedBook() != null && (exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.CANCELLED ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.SUCCESS ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.FAILURE)) {
                        boolean isRequestBookOwner = Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId());
                        boolean isOfferedBookOwner = Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId());
                        if(isRequestBookOwner || isOfferedBookOwner) {
                            if(isRequestBookOwner) {
                                exchangeRequest.get().setRequestBookOwnerReadMessages(true);
                            } else {
                                exchangeRequest.get().setOfferBookOwnerReadMessages(true);
                            }
                            model.addAttribute("user", user.get());
                            model.addAttribute("exchange_request", exchangeRequestService.save(exchangeRequest.get()));
                            return "exchange/exchange_dialogue";
                        }
                    } else {
                        return "error/400";
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @PostMapping("/dialogue")
    @ResponseBody
    public ResponseEntity<Message> postMessageDialogue(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageRequest request
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(request.getExchangeRequestId());
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR) {
                        boolean isRequestBookOwner = Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId());
                        boolean isOfferedBookOwner = Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId());
                        if(isRequestBookOwner || isOfferedBookOwner) {
                            if(request.getMessage().length() <= 1000) {
                                Message newMessage = new Message();
                                newMessage.setSender(user.get());
                                newMessage.setMessage(request.getMessage());
                                newMessage.setExchangeRequest(exchangeRequest.get());
                                exchangeRequest.get().getMessages().add(newMessage);
                                if(isRequestBookOwner) {
                                    exchangeRequest.get().setRequestBookOwnerReadMessages(true);
                                    exchangeRequest.get().setOfferBookOwnerReadMessages(false);
                                } else {
                                    exchangeRequest.get().setRequestBookOwnerReadMessages(false);
                                    exchangeRequest.get().setOfferBookOwnerReadMessages(true);
                                }
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
    public List<MessageDTO> getMessageHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(name = "exchangeRequestId") Long exchangeRequestId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeRequestId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.ACCEPTED_BY_INITIATOR ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.CANCELLED ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.SUCCESS ||
                            exchangeRequest.get().getStatus() == ExchangeRequestStatus.FAILURE) {
                        boolean isRequestBookOwner = Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId());
                        boolean isOfferedBookOwner = Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId());
                        if(isRequestBookOwner || isOfferedBookOwner) {
                            if(isRequestBookOwner) {
                                exchangeRequest.get().setRequestBookOwnerReadMessages(true);
                            } else {
                                exchangeRequest.get().setOfferBookOwnerReadMessages(true);
                            }
                            exchangeRequestService.save(exchangeRequest.get());
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
    public String getSelect(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchangeId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CREATED) &&
                            exchangeRequest.get().getRequestedBook().isAvailable() &&
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {

                        List<Book> selectionBooks = userAuthenticationService.findByUsername(
                                exchangeRequest
                                        .get()
                                        .getInitiator()
                                        .getUsername()
                                )
                                .get()
                                .getOwnedBooks()
                                .stream()
                                .filter(Book::isAvailable)
                                .toList();

                        model.addAttribute("user", user.get());
                        model.addAttribute("selection_books", selectionBooks);
                        model.addAttribute("exchange_id", exchangeId);
                        return "exchange/exchange_select";
                    } else {
                        return "error/403";
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @GetMapping("/select/confirm")
    public String getSelectConfirm(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "book_id", required = false) Long bookId,
            @RequestParam(name = "exchange_id") Long exchangeId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CREATED) &&
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                        if(bookId != null) {
                            Book selectedBook = bookService.getBookById(bookId);
                            if (selectedBook != null &&
                                    selectedBook.isAvailable() &&
                                    Objects.equals(selectedBook.getOwner().getId(), exchangeRequest.get().getInitiator().getId())) {

                                exchangeRequest.get().setOfferedBook(selectedBook);
                                exchangeRequest.get().setStatus(ExchangeRequestStatus.ACCEPTED_BY_OWNER);
                                exchangeRequestService.save(exchangeRequest.get());
                                return "redirect:/exchange/view";
                            }
                        } else {
                            return "error/400";
                        }
                    } else if(exchangeRequest.get().getOfferedBook() != null  &&
                            exchangeRequest.get().getOfferedBook().isAvailable()&&
                            exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.ACCEPTED_BY_OWNER) &&
                            Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId())) {

                        exchangeRequest.get().setStatus(ExchangeRequestStatus.ACCEPTED_BY_INITIATOR);
                        exchangeRequestService.save(exchangeRequest.get());
                        return "redirect:/exchange/view";
                    } else {
                        return "error/403";
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @GetMapping("/confirm")
    public String getConfirm(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchangeId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.ACCEPTED_BY_INITIATOR) &&
                            exchangeRequest.get().getRequestedBook().isAvailable() &&
                            exchangeRequest.get().getOfferedBook().isAvailable()) {

                        if(Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                            if(!exchangeRequest.get().isRequestBookOwnerConfirm()) {
                                model.addAttribute("user", user.get());
                                exchangeRequest.get().setRequestBookOwnerConfirm(true);
                                ExchangeRequest savedExchangeRequest = exchangeRequestService.save(exchangeRequest.get());
                                if(savedExchangeRequest.isRequestBookOwnerConfirm() && savedExchangeRequest.isOfferBookOwnerConfirm()) {
                                    savedExchangeRequest.getRequestedBook().setAvailable(false);
                                    savedExchangeRequest.getOfferedBook().setAvailable(false);
                                    savedExchangeRequest.setStatus(ExchangeRequestStatus.SUCCESS);
                                    exchangeRequestService.save(savedExchangeRequest);
                                }
                                return "exchange/exchange_confirm";
                            }
                        } else if(Objects.equals(exchangeRequest.get().getOfferedBook().getOwner().getId(), user.get().getId())) {
                            if(!exchangeRequest.get().isOfferBookOwnerConfirm()) {
                                model.addAttribute("user", user.get());
                                exchangeRequest.get().setOfferBookOwnerConfirm(true);
                                exchangeRequestService.save(exchangeRequest.get());
                                ExchangeRequest savedExchangeRequest = exchangeRequestService.save(exchangeRequest.get());
                                if(savedExchangeRequest.isRequestBookOwnerConfirm() && savedExchangeRequest.isOfferBookOwnerConfirm()) {
                                    savedExchangeRequest.getRequestedBook().setAvailable(false);
                                    savedExchangeRequest.getOfferedBook().setAvailable(false);
                                    savedExchangeRequest.setStatus(ExchangeRequestStatus.SUCCESS);
                                    exchangeRequestService.save(savedExchangeRequest);
                                }
                                return "exchange/exchange_confirm";
                            }
                        }
                    }
                    return "error/403";
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @GetMapping("/failure")
    public String getFailure(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchangeId
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if((exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CANCELLED) &&
                            exchangeRequest.get().getOfferedBook() != null &&
                            exchangeRequest.get().getRequestedBook() != null) ||
                            exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.ACCEPTED_BY_INITIATOR)) {
                        if(Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId()) ||
                                Objects.equals(exchangeRequest.get().getOfferedBook().getOwner().getId(), user.get().getId())) {
                            model.addAttribute("user", user.get());
                            exchangeRequest.get().setStatus(ExchangeRequestStatus.FAILURE);
                            exchangeRequestService.save(exchangeRequest.get());
                            return "exchange/exchange_failure";
                        }
                    }
                    return "error/403";
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }

    @PostMapping("/rating")
    public String postUserRating(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "informationAccuracy") Integer informationAccuracy,
            @RequestParam(name = "shippingSpeed") Integer shippingSpeed,
            @RequestParam(name = "overallExperience") Integer overallExperience
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchangeId);
                if (exchangeRequest.isPresent()) {
                    if(exchangeRequest.get().getStatus() == ExchangeRequestStatus.SUCCESS) {
                        boolean isRequestBookOwner = Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId());
                        boolean isOfferedBookOwner = Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId());
                        if(isRequestBookOwner && !exchangeRequest.get().isRequestBookOwnerRatedOfferBookOwner()) {
                            UserRating userRating = new UserRating();
                            userRating.setRater(exchangeRequest.get().getRequestedBook().getOwner());
                            userRating.setRatee(exchangeRequest.get().getOfferedBook().getOwner());
                            userRating.setInformationAccuracy(informationAccuracy);
                            userRating.setShippingSpeed(shippingSpeed);
                            userRating.setOverallExperience(overallExperience);

                            UserRating savedUserRating = userRatingService.save(userRating);
                            if(savedUserRating != null) {
                                Optional<User> userNeededRatingUpdate = userAuthenticationService.findByUsername(
                                        exchangeRequest
                                                .get()
                                                .getOfferedBook()
                                                .getOwner()
                                                .getUsername()
                                );
                                if(userNeededRatingUpdate.isPresent()) {
                                    List<UserRating> userRatingList = userRatingService.getUserRatingByRateeId(savedUserRating.getRatee().getId());

                                    if(!userRatingList.isEmpty()) {
                                        double informationAccuracyTemp = 0;
                                        double overallExperienceTemp = 0;
                                        double shippingSpeedTemp = 0;

                                        for (UserRating rating : userRatingList) {
                                            informationAccuracyTemp += rating.getInformationAccuracy();
                                            overallExperienceTemp += rating.getOverallExperience();
                                            shippingSpeedTemp += rating.getShippingSpeed();
                                        }

                                        informationAccuracyTemp /= userRatingList.size();
                                        overallExperienceTemp /= userRatingList.size();
                                        shippingSpeedTemp /= userRatingList.size();

                                        userNeededRatingUpdate.get().setAverageInformationAccuracy(informationAccuracyTemp);
                                        userNeededRatingUpdate.get().setAverageOverallExperience(overallExperienceTemp);
                                        userNeededRatingUpdate.get().setAverageShippingSpeed(shippingSpeedTemp);

                                        exchangeRequest.get().setRequestBookOwnerRatedOfferBookOwner(true);
                                        exchangeRequestService.save(exchangeRequest.get());
                                        return "redirect:/exchange/view";
                                    }
                                }
                            }
                        } else if(isOfferedBookOwner && !exchangeRequest.get().isOfferBookOwnerRatedRequestBookOwner()) {
                            UserRating userRating = new UserRating();
                            userRating.setRater(exchangeRequest.get().getOfferedBook().getOwner());
                            userRating.setRatee(exchangeRequest.get().getRequestedBook().getOwner());
                            userRating.setInformationAccuracy(informationAccuracy);
                            userRating.setShippingSpeed(shippingSpeed);
                            userRating.setOverallExperience(overallExperience);

                            UserRating savedUserRating = userRatingService.save(userRating);
                            if(savedUserRating != null) {
                                Optional<User> userNeededRatingUpdate = userAuthenticationService.findByUsername(
                                        exchangeRequest
                                                .get()
                                                .getRequestedBook()
                                                .getOwner()
                                                .getUsername()
                                );
                                if(userNeededRatingUpdate.isPresent()) {
                                    List<UserRating> userRatingList = userRatingService.getUserRatingByRateeId(savedUserRating.getRatee().getId());

                                    if(!userRatingList.isEmpty()) {
                                        double informationAccuracyTemp = 0;
                                        double overallExperienceTemp = 0;
                                        double shippingSpeedTemp = 0;

                                        for (UserRating rating : userRatingList) {
                                            informationAccuracyTemp += rating.getInformationAccuracy();
                                            overallExperienceTemp += rating.getOverallExperience();
                                            shippingSpeedTemp += rating.getShippingSpeed();
                                        }

                                        informationAccuracyTemp /= userRatingList.size();
                                        overallExperienceTemp /= userRatingList.size();
                                        shippingSpeedTemp /= userRatingList.size();

                                        userNeededRatingUpdate.get().setAverageInformationAccuracy(informationAccuracyTemp);
                                        userNeededRatingUpdate.get().setAverageOverallExperience(overallExperienceTemp);
                                        userNeededRatingUpdate.get().setAverageShippingSpeed(shippingSpeedTemp);

                                        exchangeRequest.get().setOfferBookOwnerRatedRequestBookOwner(true);
                                        exchangeRequestService.save(exchangeRequest.get());
                                        return "redirect:/exchange/view";
                                    }
                                }
                            }
                        }
                        return "error/400";
                    }
                }
            }
        }
        return "error/401";
    }

    @GetMapping("/cancel")
    public String postCancel(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "exchange_id") Long exchange_id
    ) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if(user.isPresent()) {
                Optional<ExchangeRequest> exchangeRequest = exchangeRequestService.getExchangeRequestById(exchange_id);
                if(exchangeRequest.isPresent()) {
                    if(Objects.equals(exchangeRequest.get().getInitiator().getId(), user.get().getId()) ||
                            Objects.equals(exchangeRequest.get().getRequestedBook().getOwner().getId(), user.get().getId())) {
                        if(!exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.CANCELLED) &&
                                !exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.SUCCESS) &&
                                !exchangeRequest.get().getStatus().equals(ExchangeRequestStatus.FAILURE)) {
                            exchangeRequest.get().setStatus(ExchangeRequestStatus.CANCELLED);
                            exchangeRequestService.save(exchangeRequest.get());
                            return "redirect:/exchange/view";
                        }
                    }
                } else {
                    return "error/404";
                }
            }
        }
        return "error/401";
    }
}
