package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.repository.PromptRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
    private final PromptRepository promptRepository;

    public List<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }

    public Optional<Prompt> getPromptById(Long id) {
        return promptRepository.findById(id);
    }

    @Override
    public List<Prompt> getPromptByTypeName(String promptTypeName) {
        return promptRepository.findByPromptTypeName(promptTypeName);
    }

    public Prompt savePrompt(Prompt prompt) {
        return promptRepository.save(prompt);
    }

    public void deletePrompt(Long id) {
        promptRepository.deleteById(id);
    }
}
