package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.EventUserMute;

import java.util.List;

public interface EventUserMuteService {
    List<EventUserMute> getEventMutes(Long eventId);

    EventUserMute createEventUserMute(Long eventId, Long userId);
}
