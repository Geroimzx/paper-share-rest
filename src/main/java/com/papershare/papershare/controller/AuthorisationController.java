package com.papershare.papershare.controller;

import com.papershare.papershare.model.RoleName;
import com.papershare.papershare.model.User;
import com.papershare.papershare.model.UserDetailsImpl;
import com.papershare.papershare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/user/auth")
public class AuthorisationController {

    @Autowired
    private UserService userService;

    @GetMapping("/sign_up")
    public String sign_up_get(Model model, @ModelAttribute("user") User user) {
        System.out.println("signup in");
        return "sign_up";
    }

    @PostMapping("/sign_up")
    public String sign_up_post(Model model, @ModelAttribute("user") User user) {
        System.out.println(user.getUsername());
        if (userService.findByUsername(user.getUsername()).isEmpty()) {
            user.setRoles(RoleName.ROLE_USER.name());
            userService.createUser(user);
            return "redirect:/user/auth/sign_in?success";
        } else {
            return "redirect:/user/auth/sign_in?error";
        }
    }

    @GetMapping("/sign_in")
    public String login_get(Model model, @ModelAttribute("user") User user) {
        System.out.println("login in");
        return "sign_in";
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login_processing")
    public String login_post(Model model, @ModelAttribute("user") User user) {
        System.out.println("login attempt for user: " + user.getUsername());

        // Create UsernamePasswordAuthenticationToken with user credentials
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        try {
            // Attempt authentication using AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Login successful, set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to intended page after successful login
            return "redirect:/some"; // Replace with your intended success page

        } catch (BadCredentialsException e) {
            // Login failed, handle bad credentials error
            model.addAttribute("error", "Invalid username or password");
            return "sign_in"; // Redirect back to login page with error message
        } catch (Exception e) {
            // Handle other exceptions (e.g., database issues)
            model.addAttribute("error", "An unexpected error occurred");
            return "sign_in"; // Redirect back to login page with generic error message
        }
    }


    @RequestMapping("/log_out")
    public String logout() {
        return "log_out";
    }

}
