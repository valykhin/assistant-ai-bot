package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.User;

import java.util.List;

public interface UserMessageHandler {
    List<String> sendMessageToAI(String message, User user);

    String determineUserMessageCategory(String message);
}
