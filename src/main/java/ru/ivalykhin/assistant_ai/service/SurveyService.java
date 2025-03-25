package ru.ivalykhin.assistant_ai.service;

import ru.ivalykhin.assistant_ai.model.Survey;

import java.util.List;
import java.util.Optional;

public interface SurveyService {
    List<Survey> getAllSurveys();

    Optional<Survey> getSurveyById(Long id);

    Survey saveSurvey(Survey survey);

    void deleteSurvey(Long id);
}
