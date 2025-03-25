package ru.ivalykhin.assistant_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivalykhin.assistant_ai.model.Prompt;

import java.util.List;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByPromptTypeName(String PromptTypeName);
}
