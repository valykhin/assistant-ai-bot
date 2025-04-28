package ru.ivalykhin.assistant_ai.bot.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ivalykhin.assistant_ai.model.EventUserMute;
import ru.ivalykhin.assistant_ai.service.EventUserMuteService;

@Component
@RequiredArgsConstructor
public class EventUserMuteCallback implements CallbackHandler {
    private final EventUserMuteService eventUserMuteService;
    public SendMessage apply(CallbackData callbackData, Update update) {
        long userId = update.getCallbackQuery().getFrom().getId();
        long eventId = Long.parseLong(callbackData.content);
        EventUserMute eventUserMute = eventUserMuteService.createEventUserMute(eventId, userId);

        return new SendMessage(String.valueOf(userId), "Принято, больше не буду.");
    }
}
