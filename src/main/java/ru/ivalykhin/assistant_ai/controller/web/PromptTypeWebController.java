package ru.ivalykhin.assistant_ai.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ivalykhin.assistant_ai.model.PromptType;
import ru.ivalykhin.assistant_ai.service.PromptTypeService;

@Controller
@RequestMapping("/prompt-types")
@RequiredArgsConstructor
public class PromptTypeWebController {
    private final PromptTypeService promptTypeService;

    @GetMapping
    public String listPromptTypes(Model model) {
        model.addAttribute("promptTypes", promptTypeService.findAll());
        return "prompt_type/list";
    }

    @GetMapping("/create")
    public String createPromptTypeForm(Model model) {
        model.addAttribute("promptType", new PromptType());
        return "prompt_type/form";
    }

    @PostMapping
    public String createPromptType(@ModelAttribute PromptType promptType, BindingResult result) {
        if (result.hasErrors()) {
            return "prompt_type/form";
        }
        promptTypeService.save(promptType);
        return "redirect:/prompt-types";
    }

    @GetMapping("/edit/{name}")
    public String editPromptTypeForm(@PathVariable String name, Model model) {
        PromptType promptType = promptTypeService.getByName(name).orElseThrow();
        model.addAttribute("promptType", promptType);
        return "prompt_type/form";
    }

    @PostMapping("/update/{name}")
    public String updatePromptType(@PathVariable String name, @ModelAttribute PromptType promptType) {
        promptType.setName(name);
        promptTypeService.save(promptType);
        return "redirect:/prompt-types";
    }

    @GetMapping("/delete/{name}")
    public String deletePromptType(@PathVariable String name) {
        promptTypeService.delete(name);
        return "redirect:/prompt-types";
    }
}
