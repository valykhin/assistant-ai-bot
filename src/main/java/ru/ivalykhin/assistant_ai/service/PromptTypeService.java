package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.PromptType;

import java.util.List;
import java.util.Optional;

public interface PromptTypeService {
    List<PromptType> findAll();

    List<PromptType> getPromptTypesWithCriteria();

    Optional<PromptType> getByName(String name);

    PromptType save(PromptType promptType);

    void delete(String name);
}
