package ru.ivalykhin.assistant_ai.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ivalykhin.assistant_ai.model.User;
import ru.ivalykhin.assistant_ai.service.UserMessageHandler;
import ru.ivalykhin.assistant_ai.service.UserService;

import java.util.List;

@Slf4j
@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final UserMessageHandler userMessageHandler;
    private final UserService userService;

    private final String botUsername;

    public TelegramBot(TelegramBotsApi telegramBotsApi,
                       @Value("${bot.name}") String botUsername,
                       @Value("${bot.token}") String botToken,
                       CommandsHandler commandsHandler,
                       CallbacksHandler callbacksHandler,
                       UserMessageHandler userMessageHandler,
                       UserService userService) throws TelegramApiException {
        super(botToken);
        this.userService = userService;
        log.info("Creating telegram long polling component");
        this.botUsername = botUsername;

        telegramBotsApi.registerBot(this);

        this.commandsHandler = commandsHandler;
        this.callbacksHandler = callbacksHandler;
        this.userMessageHandler = userMessageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(String.format("Received update: %s", update));
        if (update.hasMessage() && update.getMessage().hasText()) {

            if (this.isUserMessageForBot(update.getMessage())) {
                handleUserMessage(update);
            }

        } else if (update.hasCallbackQuery()) {
            log.info(String.format("Received callback query message: %s",
                    update.getCallbackQuery().toString()));
            sendMessage(callbacksHandler.handleCallbacks(update));
        } else if (update.hasChannelPost()) {
            String channelName = update.getChannelPost().getChat().getTitle();
            Long channelId = update.getChannelPost().getChatId();
            String messageText = update.getChannelPost().getText();

            log.info("Received channel message: " + channelName + " [" + channelId + "]: " + messageText);
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        log.info("Send to chat {} message: {}", sendMessage.getChatId(), sendMessage.getText());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private Boolean isUserMessageForBot(Message message) {
        log.info("@" + getBotUsername());
        return "private".equals(message.getChat().getType())
                || ("supergroup".equals(message.getChat().getType())
                    && message.getText().contains("@" + getBotUsername()));
    }

    private void handleUserMessage(Update update) {
        Message message = update.getMessage();
        String messageText = message.getText();
        log.info(String.format("Received from user %s message: %s",
                message.getFrom().getId(),
                messageText));

        String chatId = message.getChatId().toString();

        User user = userService.registerUser(
                message.getFrom().getId(),
                message.getFrom().getUserName(),
                message.getFrom().getFirstName(),
                message.getFrom().getLastName());

        if (messageText.startsWith("/")) {
            sendMessage(commandsHandler.handleCommands(update));
        } else {
            if (message.getReplyToMessage() != null) {
                Message replyToMessage = message.getReplyToMessage();
                messageText = String.format("@%s\n%s\n%s",
                        replyToMessage.getFrom().getUserName(),
                        replyToMessage.getText(),
                        messageText);
            }
            List<String> responseList = userMessageHandler.sendMessageToAI(
                    messageText,
                    user
            );

            String response = String.join("\n", responseList);
            SendMessage newMessage = new SendMessage(chatId, response);

            if ("supergroup".equals(message.getChat().getType())) {
                newMessage.setReplyToMessageId(message.getMessageId());
            }

            sendMessage(newMessage);
        }
    }
}
