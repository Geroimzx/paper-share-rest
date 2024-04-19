package com.papershare.papershare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/settings")
public class SettingsController {

    @GetMapping
    public String getSettings(Model model) {
        return "settings/settings";
    }
}
