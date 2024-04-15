package com.papershare.papershare.controller;

import com.papershare.papershare.model.User;
import com.papershare.papershare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class AuthorisationController {

    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        System.out.println("register in");
        return "register";
    }

    @RequestMapping("/profile")
    public String profile() {
        return "profile";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "logout";
    }

    @PostMapping("/login")
    public String login(String username, String password) {
        if (userService.login(username, password)) {
            return "redirect:/profile";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/register")
    public String register(String username, String password, String email) {
        System.out.println(username);
        if (userService.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            userService.createUser(user);
            return "redirect:/index";
        } else {
            return "redirect:/register?error=true";
        }
    }

}
