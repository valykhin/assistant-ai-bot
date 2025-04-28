package ru.ivalykhin.assistant_ai.bot.callback;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallbackType {
    MUTE_EVENT_FOR_USER("Не получать подобные сообщения");

    private final String text;
}
