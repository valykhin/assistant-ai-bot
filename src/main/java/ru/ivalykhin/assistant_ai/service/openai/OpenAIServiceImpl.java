package ru.ivalykhin.assistant_ai.service.openai;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {
    private final OpenAIClient openAIClient;
    @Value(value = "${openai.model}")
    private String model;

    public Response createResponse(String message, String userId, String previousResponseId, String prompt) {
        log.info("Create OpenAI {} response for user {} to request: {}", model, userId, message);
        ResponseCreateParams responseCreateParams = ResponseCreateParams.builder()
                .model(model)
                .input(message)
                .user(userId)
                .previousResponseId(Optional.ofNullable(previousResponseId))
                .instructions(Optional.ofNullable(prompt))
                .build();
        log.debug("ResponseCreateParams: {}", responseCreateParams);

        return openAIClient.responses().create(responseCreateParams);
    }

    public Response createResponse(String message, String prompt) {
        log.info("Create OpenAI {} response to request: {}", model, message);
        ResponseCreateParams responseCreateParams = ResponseCreateParams.builder()
                .model(model)
                .input(message)
                .instructions(Optional.ofNullable(prompt))
                .build();
        log.debug("ResponseCreateParams: {}", responseCreateParams);

        return openAIClient.responses().create(responseCreateParams);
    }

    public List<String> getTextFromResponse(Response response) {
        return Optional.ofNullable(response)
                .map(Response::output)
                .map(output -> output.stream()
                        .map(ResponseOutputItem::message)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .flatMap(item -> item.content().stream())
                        .map(ResponseOutputMessage.Content::outputText)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(ResponseOutputText::text)
                        .toList())
                .orElse(Collections.emptyList());
    }
}
