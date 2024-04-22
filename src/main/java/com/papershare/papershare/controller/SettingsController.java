package com.papershare.papershare.controller;

import com.papershare.papershare.model.User;
import com.papershare.papershare.service.ImageUploadService;
import com.papershare.papershare.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user/settings")
public class SettingsController {

    private UserAuthenticationService userAuthenticationService;

    private ImageUploadService imageUploadService;

    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Autowired
    public void setImageUploadService(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
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
    public String postSettings(Model model, @ModelAttribute("userData") User userData, @RequestParam("uploaded-image") MultipartFile uploadedImage) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<User> authUser = userAuthenticationService.findByUsername(authentication.getName());

        if(!userData.getEmail().isBlank()) {
            if(authUser.isPresent()) {
                User updatedUser = authUser.get();

                updatedUser.setEmail(userData.getEmail());

                if(!uploadedImage.isEmpty() && Objects.requireNonNull(uploadedImage.getContentType()).startsWith("image/")) {
                    String imageUrl = imageUploadService.uploadImage(uploadedImage);

                    updatedUser.setImageUrl(imageUrl);
                } else {
                    return "redirect:/user/settings?error=imageFormatError";
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
