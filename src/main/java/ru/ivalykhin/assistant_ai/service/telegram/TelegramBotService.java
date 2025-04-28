package ru.ivalykhin.assistant_ai.service.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackData;

import java.util.List;

public interface TelegramBotService {
    void sendMessage(SendMessage sendMessage);

    InlineKeyboardMarkup addKeyboard(List<CallbackData> callbackDataList);
}
