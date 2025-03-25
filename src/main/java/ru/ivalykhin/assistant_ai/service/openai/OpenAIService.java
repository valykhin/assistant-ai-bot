package ru.ivalykhin.assistant_ai.service.openai;

import com.openai.models.responses.Response;

import java.util.List;

public interface OpenAIService {
    Response createResponse(String message, String userId, String previousResponseId, String prompt);

    Response createResponse(String message, String prompt);

    List<String> getTextFromResponse(Response response);
}
