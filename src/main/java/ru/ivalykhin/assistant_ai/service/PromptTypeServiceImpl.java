package ru.ivalykhin.assistant_ai.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.PromptType;
import ru.ivalykhin.assistant_ai.repository.PromptTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PromptTypeServiceImpl implements PromptTypeService {
    private final PromptTypeRepository promptTypeRepository;

    public List<PromptType> findAll() {
        return promptTypeRepository.findAll();
    }

    public List<PromptType> getPromptTypesWithCriteria() {
        return promptTypeRepository.findByCriteriaIsNotNullAndCriteriaNot("");
    }

    public Optional<PromptType> getByName(String name) {
        return promptTypeRepository.findById(name);
    }

    public PromptType save(PromptType promptType) {
        return promptTypeRepository.save(promptType);
    }

    public void delete(String name) {
        promptTypeRepository.deleteById(name);
    }
}

