package ru.ivalykhin.assistant_ai.bot.callback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CallbackData {
    public CallbackType type;
    public String content;
}
