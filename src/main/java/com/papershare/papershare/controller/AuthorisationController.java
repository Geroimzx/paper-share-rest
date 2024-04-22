package com.papershare.papershare.controller;

import com.papershare.papershare.model.RoleName;
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
@RequestMapping("/user/auth")
public class AuthorisationController {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping("/sign_up")
    public String sign_up_get(Model model, @ModelAttribute("user") User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> loggedUser = userAuthenticationService.findByUsername(authentication.getName());

        if(loggedUser.isPresent()) {
            return "redirect:/";
        }

        return "authentication/sign_up";
    }

    @PostMapping("/sign_up")
    public String sign_up_post(Model model, @ModelAttribute("user") User user) {
        if (userAuthenticationService.findByUsername(user.getUsername()).isEmpty()) {
            user.setRoles(RoleName.ROLE_USER.name());
            user.setImageUrl("https://storage.googleapis.com/paper-share-images/user.470x512.png");
            userAuthenticationService.createUser(user);
            return "redirect:/user/auth/sign_in?success";
        } else {
            return "redirect:/user/auth/sign_up?error";
        }
    }

    @GetMapping("/sign_in")
    public String login_get(Model model, @ModelAttribute("user") User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> loggedUser = userAuthenticationService.findByUsername(authentication.getName());

        if(loggedUser.isPresent()) {
            return "redirect:/";
        }

        return "authentication/sign_in";
    }

    @RequestMapping("/log_out")
    public String logout() {
        return "authentication/log_out";
    }

}
