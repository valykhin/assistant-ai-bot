package ru.ivalykhin.assistant_ai.bot.callback;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallbackData {
    public CallbackType type;
    public String content;
}
