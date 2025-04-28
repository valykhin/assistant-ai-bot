package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.ScheduleConfig;

import java.time.OffsetDateTime;

public interface ScheduleService {
    OffsetDateTime calculateNextExecutionTime(ScheduleConfig scheduleConfig);
}
