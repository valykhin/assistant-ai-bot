package ru.ivalykhin.assistant_ai.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.service.EventService;

import java.util.List;

@Slf4j
@Component
public class EventScheduler {

    private final EventService eventService;

    public EventScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void executeScheduledEvents() {
        log.info("Executing scheduling events");
        List<Event> events = eventService.getReadyToExecuteEvents();
        List<String> eventNames = events.stream().map(Event::getName).toList();
        log.info("Events to execute: {}", eventNames);
        for (Event event : events) {
            eventService.executeEvent(event);
        }
    }
}
