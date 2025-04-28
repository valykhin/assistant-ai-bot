package ru.ivalykhin.assistant_ai.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.model.ScheduleConfig;
import ru.ivalykhin.assistant_ai.service.EventService;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.UserService;

import java.time.OffsetTime;

@Slf4j
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventWebController {
    private final EventService eventService;
    private final PromptService promptService;
    private final UserService userService;

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "event/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Event event = new Event();
        event.setLastChangedBy(userService.getCurrentAdminUsername());
        event.setSchedule(ScheduleConfig.builder().time(OffsetTime.now()).build());

        model.addAttribute("event", event);
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "event/form";
    }

    @PostMapping
    public String createEvent(@ModelAttribute Event event,
                              @RequestParam("schedule.periodicity") String periodicity,
                              @RequestParam("schedule.time") String time,
                              @RequestParam(value = "schedule.juniorUnits", required = false) String juniorUnits,
                              @RequestParam(value = "schedule.offset", required = false) String offset,
                              @RequestParam(value = "schedule.exceptDates", required = false) String exceptDates
    ) {
        ScheduleConfig schedule = ScheduleConfig.fromFormParams(periodicity, time, juniorUnits, offset, exceptDates);
        event.setSchedule(schedule);
        log.info(event.toString());

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
