package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.ScheduleConfig;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    public OffsetDateTime calculateNextExecutionTime(ScheduleConfig scheduleConfig) {
        CronExpression cronTrigger = CronExpression.parse(scheduleConfig.getCron());

        OffsetDateTime nextExecutionTime = cronTrigger.next(OffsetDateTime.now());

        while (checkIfDateInExceptions(scheduleConfig, nextExecutionTime.toLocalDate())) {
            nextExecutionTime = nextExecutionTime.plusDays(1);
        }

        return nextExecutionTime;
    }

    private Boolean checkIfDateInExceptions(ScheduleConfig scheduleConfig, LocalDate date) {
        if (scheduleConfig.getExceptDates() == null) {
            return false;
        }
        return !scheduleConfig.getExceptDates().stream()
                .filter(exceptDate -> exceptDate.isEqual(date))
                .toList()
                .isEmpty();
    }

}
