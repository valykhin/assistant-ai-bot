package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    Event saveEvent(Event event);

    void deleteEvent(Long id);

    List<Event> getReadyToExecuteEvents();

    void executeEvent(Event event);
}
