package com.papershare.papershare.controller;

import com.papershare.papershare.service.UserAuthenticationService;
import com.papershare.papershare.service.impl.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    private UserAuthenticationService userAuthenticationService;

    private WishlistItemService wishlistItemService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setWishlistItemService(WishlistItemService wishlistItemService) {
        this.wishlistItemService = wishlistItemService;
    }

    @PostMapping("/add/{bookId}")
    @ResponseBody
    public ResponseEntity<Void> addToWishlist(@PathVariable Long bookId) {
        return ResponseEntity.ok().build();
    }
}
