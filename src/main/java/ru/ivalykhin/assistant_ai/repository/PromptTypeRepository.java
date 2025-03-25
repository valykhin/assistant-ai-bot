package ru.ivalykhin.assistant_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivalykhin.assistant_ai.model.PromptType;

import java.util.List;

@Repository
public interface PromptTypeRepository extends JpaRepository<PromptType, String> {
    List<PromptType> findByCriteriaIsNotNullAndCriteriaNot(String value);
}
