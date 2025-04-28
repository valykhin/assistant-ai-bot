package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackData;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackType;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.model.EventUserMute;
import ru.ivalykhin.assistant_ai.model.User;
import ru.ivalykhin.assistant_ai.repository.EventRepository;
import ru.ivalykhin.assistant_ai.service.telegram.TelegramBotService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserMessageHandler userMessageHandler;
    private final UserService userService;
    private final EventUserMuteService eventUserMuteService;
    private final TelegramBotService telegramBotService;
    private final ScheduleService scheduleService;

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

    public List<Event> getReadyToExecuteEvents() {
        return eventRepository.findByNextExecutionAtIsNullOrNextExecutionAtLessThanEqual(OffsetDateTime.now());
    }

    public void executeEvent(Event event) {
        log.info("Execute event: {}", event.getName());

        List<EventUserMute> eventMutes = eventUserMuteService.getEventMutes(event.getId());
        Set<Long> mutedUserIds = eventMutes.stream()
                .map(mute -> mute.getId().getUserId())
                .collect(Collectors.toSet());

        List<User> users = userService.getAllUsers().stream()
                .filter(user -> !mutedUserIds.contains(user.getId()))
                .toList();

        List<String> usernames = users.stream()
                .map(user -> String.format("%s(%s)", user.getId(), user.getUsername()))
                .toList();

        log.info("Send event: {} to users: {}", event.getName(), usernames);

        for (User user : users) {
            List<String> responseList = userMessageHandler.sendMessageToAI(
                    event.getContent(),
                    user
            );

            String response = String.join("\n", responseList);

            SendMessage message = new SendMessage(String.valueOf(user.getId()), response);
            List<CallbackData> callbackDataList = List.of(
                    new CallbackData(
                            CallbackType.MUTE_EVENT_FOR_USER,
                            String.valueOf(event.getId())));

            message.setReplyMarkup(telegramBotService.addKeyboard(callbackDataList));
            telegramBotService.sendMessage(message);
        }

        event.setLastExecutedAt(OffsetDateTime.now());
        event.setNextExecutionAt(scheduleService.calculateNextExecutionTime(event.getSchedule()));
        eventRepository.save(event);
    }
}
