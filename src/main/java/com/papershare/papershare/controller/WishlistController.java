package com.papershare.papershare.controller;

import com.papershare.papershare.model.*;
import com.papershare.papershare.service.BookService;
import com.papershare.papershare.service.UserAuthenticationService;
import com.papershare.papershare.service.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    private UserAuthenticationService userAuthenticationService;

    private BookService bookService;

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
    public void setWishlistItemService(WishlistItemService wishlistItemService) {
        this.wishlistItemService = wishlistItemService;
    }

    @GetMapping("/view")
    public String getWishlistView(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                model.addAttribute("user", user.get());
                model.addAttribute("wishlist", wishlistItemService.getAllWishlistItemsByUserId(user.get().getId()));
                return "wishlist/wishlist_view";
            }
        }
        return "error/401";
    }

    @PostMapping("/add/{bookId}")
    @ResponseBody
    public ResponseEntity<Void> addToWishlist(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long bookId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Book book = bookService.getBookById(bookId);
                if (book != null && book.isAvailable()) {
                    if(!Objects.equals(book.getOwner().getId(), user.get().getId())) {
                        if(!wishlistItemService.existsByUserIdAndBookId(user.get().getId(), bookId)) {
                            WishlistItem newWishlistItem = new WishlistItem();
                            newWishlistItem.setUser(user.get());
                            newWishlistItem.setBook(book);
                            newWishlistItem = wishlistItemService.save(newWishlistItem);
                            if(newWishlistItem != null) {
                                return ResponseEntity.status(HttpStatus.OK).build();
                            }
                        }
                    }
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/delete/{bookId}")
    @ResponseBody
    public ResponseEntity<Void> deleteFromWishlist(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long bookId) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());

            if (user.isPresent()) {
                Book book = bookService.getBookById(bookId);
                if (book != null) {
                    if(!Objects.equals(book.getOwner().getId(), user.get().getId())) {
                        if(wishlistItemService.existsByUserIdAndBookId(user.get().getId(), bookId)) {
                            wishlistItemService.delete(wishlistItemService.getWishlistItemByUserIdAndBookId(user.get().getId(), bookId).getId());
                            if(!wishlistItemService.existsByUserIdAndBookId(user.get().getId(), bookId)) {
                                return ResponseEntity.status(HttpStatus.OK).build();
                            }
                        }
                    }
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
