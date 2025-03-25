package ru.ivalykhin.assistant_ai.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}