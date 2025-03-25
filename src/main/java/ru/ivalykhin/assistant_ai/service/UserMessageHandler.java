package ru.ivalykhin.assistant_ai.service;

import java.util.List;

public interface UserMessageHandler {
    List<String> sendMessageToAI(String message, Long userId);

    String determineUserMessageCategory(String message);
}
