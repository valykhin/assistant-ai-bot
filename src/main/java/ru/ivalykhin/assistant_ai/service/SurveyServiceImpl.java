package ru.ivalykhin.assistant_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.Survey;
import ru.ivalykhin.assistant_ai.repository.SurveyRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;

    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }
}
