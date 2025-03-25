package ru.ivalykhin.assistant_ai.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackData;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackHandler;
import ru.ivalykhin.assistant_ai.bot.callback.CallbackType;
import ru.ivalykhin.assistant_ai.bot.callback.TestCallback;

import java.util.Map;

@Component
public class CallbacksHandler {
    private final Map<CallbackType, CallbackHandler> callbacks;
    private final ObjectMapper objectMapper;

    public CallbacksHandler(TestCallback testCallback, ObjectMapper objectMapper) {
        this.callbacks = Map.of(CallbackType.TEST, testCallback
        );
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public SendMessage handleCallbacks(Update update) {
        CallbackData callbackData = objectMapper.readValue(update.getCallbackQuery().getData(), CallbackData.class);
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMessage answer;
        if (callbackData == null) {
            answer = new SendMessage(String.valueOf(chatId), DefaultMessages.UNKNOWN_COMMAND);
        } else {
            CallbackHandler callbackBiFunction = callbacks.get(callbackData.getType());
            answer = callbackBiFunction.apply(callbackData, update);
        }

        return answer;
    }
}
