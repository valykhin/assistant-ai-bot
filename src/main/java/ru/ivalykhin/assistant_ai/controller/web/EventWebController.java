package ru.ivalykhin.assistant_ai.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.EventService;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventWebController {
    private final EventService eventService;
    private final PromptService promptService;

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "event/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "event/form";
    }

    @PostMapping
    public String createEvent(@ModelAttribute Event event) {
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id).orElseThrow());
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "event/form";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event) {
        event.setId(id);
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }
}
