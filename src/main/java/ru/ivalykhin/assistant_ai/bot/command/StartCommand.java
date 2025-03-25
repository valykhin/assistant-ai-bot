package ru.ivalykhin.assistant_ai.bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ivalykhin.assistant_ai.bot.DefaultMessages;

@RequiredArgsConstructor
@Component
public class StartCommand implements Command {

    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(DefaultMessages.WELCOME);

        return sendMessage;
    }
}