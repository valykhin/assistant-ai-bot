package ru.ivalykhin.assistant_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivalykhin.assistant_ai.model.EventUserMute;

import java.util.List;

@Repository
public interface EventUserMuteRepository extends JpaRepository<EventUserMute, Long> {
    List<EventUserMute> getByEventId(Long eventId);
}
