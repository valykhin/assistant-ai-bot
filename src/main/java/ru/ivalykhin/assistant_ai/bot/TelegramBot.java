package ru.ivalykhin.assistant_ai.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ivalykhin.assistant_ai.service.UserMessageHandler;

import java.util.List;

@Slf4j
@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserMessageHandler userMessageHandler;

    private final String botUsername;

    public TelegramBot(TelegramBotsApi telegramBotsApi,
                       @Value("${bot.name}") String botUsername,
                       @Value("${bot.token}") String botToken,
                       CommandsHandler commandsHandler,
                       CallbacksHandler callbacksHandler,
                       UserMessageHandler userMessageHandler) throws TelegramApiException {
        super(botToken);
        log.info("Creating telegram long polling component");
        this.botUsername = botUsername;

        telegramBotsApi.registerBot(this);

        this.commandsHandler = commandsHandler;
        this.callbacksHandler = callbacksHandler;
        this.userMessageHandler = userMessageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug(String.format("Received update: %s", update));
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            log.info(String.format("Received from user %s message: %s",
                    update.getMessage().getFrom().getId(),
                    message));

            String chatId = update.getMessage().getChatId().toString();

            if (message.startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            } else {
                List<String> responseList = userMessageHandler.sendMessageToAI(
                        message,
                        update.getMessage().getFrom().getId()
                );
                responseList.forEach(response -> sendMessage(new SendMessage(chatId, response)));
            }

        } else if (update.hasCallbackQuery()) {
            log.info(String.format("Received callback query message: %s",
                    update.getCallbackQuery().toString()));
            sendMessage(callbacksHandler.handleCallbacks(update));
        }
    }

    private void sendMessage(SendMessage sendMessage) {
        log.info("Send to chat {} message: {}", sendMessage.getChatId(), sendMessage.getText());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
