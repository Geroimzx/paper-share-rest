package com.papershare.papershare.controller;

import com.papershare.papershare.model.User;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/user/settings")
public class SettingsController {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping
    public String getSettings(Model model, @ModelAttribute("userData") User userData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> authUser = userAuthenticationService.findByUsername(authentication.getName());

        if(authUser.isPresent()) {
            model.addAttribute("userData", authUser.get());
            model.addAttribute("user", authUser.get());
        }

        return "settings/settings";
    }

    @PostMapping
    public String postSettings(Model model, @ModelAttribute("userData") User userData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> authUser = userAuthenticationService.findByUsername(authentication.getName());

        if(!userData.getEmail().isBlank()) {
            if(authUser.isPresent()) {
                User updatedUser = authUser.get();

                updatedUser.setEmail(userData.getEmail());

                if(userData.getImageUrl() != null) {
                    updatedUser.setImageUrl(userData.getImageUrl());
                }

                updatedUser.setFirstName(userData.getFirstName());
                updatedUser.setLastName(userData.getLastName());

                updatedUser = userAuthenticationService.updateUser(updatedUser);

                if(updatedUser != null) {
                    return "redirect:/user/settings?success=true";
                }
            }
        }
        System.out.println("error update or blank");
        return "redirect:/user/settings?error=saveError";
    }
}
