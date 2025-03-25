package ru.ivalykhin.assistant_ai.bot.callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    SendMessage apply(CallbackData callback, Update update);
}
