package ru.ivalykhin.assistant_ai.bot.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TestCallback implements CallbackHandler {
    public SendMessage apply(CallbackData callback, Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        return new SendMessage(String.valueOf(chatId), "test callback");
    }
}
