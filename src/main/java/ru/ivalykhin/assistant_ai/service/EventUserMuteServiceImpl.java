package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.model.EventUserMute;
import ru.ivalykhin.assistant_ai.model.EventUserMuteId;
import ru.ivalykhin.assistant_ai.repository.EventUserMuteRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventUserMuteServiceImpl implements EventUserMuteService {
    private final EventUserMuteRepository userMuteRepository;
    public List<EventUserMute> getEventMutes(Long eventId) {
        return userMuteRepository.getByEventId(eventId);
    }

    public EventUserMute createEventUserMute(Long eventId, Long userId) {
        Event event = new Event();
        event.setId(eventId);
        EventUserMute eventUserMute = EventUserMute.builder()
                .id(new EventUserMuteId(eventId, userId))
                .event(event)
                .blockedAt(OffsetDateTime.now())
                .build();
        return userMuteRepository.save(eventUserMute);
    }
}
