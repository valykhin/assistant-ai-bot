package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.Prompt;

import java.util.List;
import java.util.Optional;

public interface PromptService {
    List<Prompt> getAllPrompts();

    Optional<Prompt> getPromptById(Long id);

    List<Prompt> getPromptByTypeName(String promptTypeName);

    Prompt savePrompt(Prompt prompt);

    void deletePrompt(Long id);
}
