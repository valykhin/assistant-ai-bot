package ru.ivalykhin.assistant_ai.service;

import com.openai.models.responses.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.model.PromptType;
import ru.ivalykhin.assistant_ai.model.User;
import ru.ivalykhin.assistant_ai.service.openai.OpenAIService;

import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageHandlerImpl implements UserMessageHandler {
    private final OpenAIService openAIService;
    private final UserService userService;
    private final PromptService promptService;
    private final PromptTypeService promptTypeService;

    @Value("${openai.default-prompt}")
    private String DEFAULT_PROMPT;

    @Override
    public List<String> sendMessageToAI(String message, Long userId) {
        User user = userService.getUserById(userId)
                .orElse(userService.createNewUser(userId));

        String prompt = getSuggestedPrompt(message);

        Response response =
                openAIService.createResponse(
                        message,
                        userId.toString(),
                        user.getLastAIResponseId(),
                        prompt);

        if (response != null) {
            user.setLastAIResponseId(response.id());
            userService.saveUser(user);
        }

        return openAIService.getTextFromResponse(response);
    }

    private String getSuggestedPrompt(String message) {
        String suggestedCategory = determineUserMessageCategory(message);
        log.info("Suggested category is {}", suggestedCategory);
        List<Prompt> suggestedPromptList = promptService.getPromptByTypeName(suggestedCategory);
        return suggestedPromptList.isEmpty() ? DEFAULT_PROMPT : suggestedPromptList.get(0).getContent();
    }

    public String determineUserMessageCategory(String message) {
        Response response = null;

        List<PromptType> promptTypeList = promptTypeService.getPromptTypesWithCriteria();

        String promptTypes = promptTypeList
                .stream()
                .map(promptType -> String.format("%s - %s", promptType.getName(), promptType.getCriteria()))
                .collect(joining("\n"));

        List<Prompt> suggestedPromptList = promptService.getPromptByTypeName("category_auto_definition");

        if (!suggestedPromptList.isEmpty() && StringUtils.isNoneEmpty(promptTypes)) {
            String prompt = String.format("%s\n%s", suggestedPromptList.get(0).getContent(), promptTypes);
            response = openAIService.createResponse(message, prompt);
        }

        List<String> categories = openAIService.getTextFromResponse(response);

        return categories.isEmpty() ? "" : promptTypeList.stream()
                .filter(promptType -> categories.get(0).contains(promptType.getName()))
                .findFirst()
                .map(PromptType::getName)
                .orElse("");
    }
}
