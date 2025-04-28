package ru.ivalykhin.assistant_ai.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.PromptTypeService;
import ru.ivalykhin.assistant_ai.service.UserService;

@Slf4j
@Controller
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptWebController {
    private final PromptService promptService;
    private final PromptTypeService promptTypeService;
    private final UserService userService;

    @GetMapping
    public String listPrompts(Model model) {
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "prompt/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Prompt prompt = new Prompt();
        prompt.setLastChangedBy(userService.getCurrentAdminUsername());

        model.addAttribute("prompt", prompt);
        model.addAttribute("promptTypes", promptTypeService.findAll());
        return "prompt/form";
    }

    @PostMapping
    public String createPrompt(@ModelAttribute Prompt prompt) {
        promptService.savePrompt(prompt);
        return "redirect:/prompts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Prompt prompt = promptService.getPromptById(id).orElseThrow();
        prompt.setLastChangedBy(userService.getCurrentAdminUsername());

        model.addAttribute("prompt", prompt);
        model.addAttribute("promptTypes", promptTypeService.findAll());
        return "prompt/form";
    }

    @PostMapping("/update/{id}")
    public String updatePrompt(@PathVariable Long id, @ModelAttribute Prompt prompt) {
        prompt.setId(id);
        promptService.savePrompt(prompt);
        return "redirect:/prompts";
    }

    @GetMapping("/delete/{id}")
    public String deletePrompt(@PathVariable Long id) {
        promptService.deletePrompt(id);
        return "redirect:/prompts";
    }
}
