package com.papershare.papershare.controller;

import com.papershare.papershare.model.User;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping("/{username}")
    public String getUserById(@AuthenticationPrincipal UserDetails userDetails, Model model, @PathVariable("username") String username) {
        if(userDetails != null) {
            Optional<User> user = userAuthenticationService.findByUsername(userDetails.getUsername());


            user.ifPresent(value -> model.addAttribute("user", value));
        }
        Optional<User> userByUsername = userAuthenticationService.findByUsername(username);
        if(userByUsername.isPresent()) {
            model.addAttribute("user_by_id", userByUsername.get());
            return "user/user";
        }
        return "error/404";
    }
}
