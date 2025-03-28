package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
