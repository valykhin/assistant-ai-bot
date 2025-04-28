package ru.ivalykhin.assistant_ai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "event_user_mutes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUserMute {
    @EmbeddedId
    private EventUserMuteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "blocked_at", nullable = false)
    private OffsetDateTime blockedAt = OffsetDateTime.now();
}
