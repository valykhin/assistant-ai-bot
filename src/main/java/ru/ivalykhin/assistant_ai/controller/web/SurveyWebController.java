package ru.ivalykhin.assistant_ai.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ivalykhin.assistant_ai.model.Survey;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.SurveyService;

@Controller
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyWebController {
    private final SurveyService surveyService;
    private final PromptService promptService;

    @GetMapping
    public String listSurveys(Model model) {
        model.addAttribute("surveys", surveyService.getAllSurveys());
        return "survey/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("survey", new Survey());
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "survey/form";
    }

    @PostMapping
    public String createSurvey(@ModelAttribute Survey survey) {
        surveyService.saveSurvey(survey);
        return "redirect:/surveys";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("survey", surveyService.getSurveyById(id).orElseThrow());
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "survey/form";
    }

    @PostMapping("/update/{id}")
    public String updateSurvey(@PathVariable Long id, @ModelAttribute Survey survey) {
        survey.setId(id);
        surveyService.saveSurvey(survey);
        return "redirect:/surveys";
    }

    @GetMapping("/delete/{id}")
    public String deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return "redirect:/surveys";
    }
}
