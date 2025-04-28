package ru.ivalykhin.assistant_ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleConfig {
    private Periodicity periodicity;
    @DateTimeFormat(pattern = "HH:mm:ssXXX")
    private OffsetTime time;
    private List<Integer> juniorUnits; // Подчинённые единицы (например, дни недели для WEEKLY или дни месяца для MONTHLY)
    private List<Integer> offset;
    private List<LocalDate> exceptDates;

    public static ScheduleConfig fromFormParams(
            String periodicity,
            String time,
            String juniorUnits,
            String offset,
            String exceptDates
    ) {
        ScheduleConfigBuilder configBuilder = ScheduleConfig.builder()
                .periodicity(Periodicity.valueOf(periodicity));

        if (StringUtils.isNotBlank(time)) {
            configBuilder.time(OffsetTime.parse(time));
        }

        if (StringUtils.isNotBlank(juniorUnits)) {
            configBuilder.juniorUnits(parseIntList(juniorUnits));
        }

        if (StringUtils.isNotBlank(offset)) {
            configBuilder.offset(parseIntList(offset));
        }

        if (StringUtils.isNotBlank(exceptDates)) {
            configBuilder.exceptDates(Arrays.stream(exceptDates.split(","))
                    .map(String::trim)
                    .map(LocalDate::parse)
                    .collect(Collectors.toList()));
        }

        return configBuilder.build();
    }

    private static List<Integer> parseIntList(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public String getCron() {
        int minute = this.time.getMinute();
        int hour = this.time.getHour();

        String cronMinute = String.valueOf(minute);
        String cronHour = String.valueOf(hour);
        String cronDayOfMonth = "*";
        String cronMonth = "*";
        String cronDayOfWeek = "?";

        switch (this.getPeriodicity()) {
            case DAILY -> {
                // Каждый день в указанное время
                if (CollectionUtils.isNotEmpty(this.juniorUnits)) {
                    cronHour = this.juniorUnits.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                } else {
                    cronHour = String.valueOf(time.getHour());
                }
                cronDayOfWeek = "?";
            }
            case WEEKLY -> {
                // Выполняем в указанные дни недели (1 = Пн, 7 = Вс)
                if (CollectionUtils.isNotEmpty(this.juniorUnits)) {
                    cronDayOfWeek = this.juniorUnits.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                }
            }
            case MONTHLY -> {
                // Выполняем в указанные дни месяца
                if (CollectionUtils.isNotEmpty(this.juniorUnits)) {
                    cronDayOfMonth = this.juniorUnits.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                }
            }
            case YEARLY -> {
                // Выполняем в указанные месяцы
                if (CollectionUtils.isNotEmpty(this.juniorUnits)) {
                    cronMonth = this.juniorUnits.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                }
            }
            default -> throw new IllegalArgumentException("Unsupported periodicity: " + this.periodicity);
        }

        return String.format("0 %s %s %s %s %s", cronMinute, cronHour, cronDayOfMonth, cronMonth, cronDayOfWeek);
    }
}
