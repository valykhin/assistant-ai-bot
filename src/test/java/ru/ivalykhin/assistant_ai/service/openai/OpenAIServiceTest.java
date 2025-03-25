package ru.ivalykhin.assistant_ai.service.openai;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.*;
import com.openai.services.blocking.ResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.ivalykhin.assistant_ai.bot.TelegramBot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OpenAIServiceTest {
    @MockitoBean
    private OpenAIClient openAIClient;
    @MockitoBean
    private TelegramBot telegramBot;
    @Mock
    private ResponseService responseService;
    @Autowired
    private OpenAIService openAIService;

    private static final String MESSAGE = "test message";
    private static final String USER_ID = "user123";
    private static final String PREVIOUS_RESPONSE_ID = "resp_123";
    private static final String PROMPT = "talk like pirate";
    @Value(value = "${openai.model}")
    private String MODEL;
    private Response response;

    @BeforeEach
    public void setUp() {
        response = mock(Response.class);
    }

    @Test
    public void createResponseForUser_success() {
        ResponseCreateParams responseCreateParams = ResponseCreateParams.builder()
                .model(MODEL)
                .input(MESSAGE)
                .user(USER_ID)
                .previousResponseId(Optional.of(PREVIOUS_RESPONSE_ID))
                .instructions(Optional.of(PROMPT))
                .build();

        when(openAIClient.responses()).thenReturn(responseService);
        when(openAIClient.responses().create(responseCreateParams)).thenReturn(response);

        Response result = openAIService.createResponse(MESSAGE, USER_ID, PREVIOUS_RESPONSE_ID, PROMPT);

        verify(responseService, times(1)).create(responseCreateParams);
        assertEquals(response, result);
    }

    @Test
    public void createResponse_success() {
        ResponseCreateParams responseCreateParams = ResponseCreateParams.builder()
                .model(MODEL)
                .input(MESSAGE)
                .instructions(Optional.of(PROMPT))
                .build();

        when(openAIClient.responses()).thenReturn(responseService);
        when(openAIClient.responses().create(responseCreateParams)).thenReturn(response);

        Response result = openAIService.createResponse(MESSAGE, PROMPT);

        verify(responseService).create(responseCreateParams);
        assertEquals(response, result);
    }

    @Test
    public void testGetTextFromResponse() {
        ResponseOutputItem outputItem = mock(ResponseOutputItem.class);
        ResponseOutputMessage responseOutputMessage = mock(ResponseOutputMessage.class);
        ResponseOutputMessage.Content content = mock(ResponseOutputMessage.Content.class);
        ResponseOutputText outputText = mock(ResponseOutputText.class);

        when(response.output()).thenReturn(List.of(outputItem));
        when(outputItem.message()).thenReturn(Optional.of(responseOutputMessage));
        when(responseOutputMessage.content()).thenReturn(List.of(content));
        when(content.outputText()).thenReturn(Optional.of(outputText));
        when(outputText.text()).thenReturn("Some text");

        List<String> texts = openAIService.getTextFromResponse(response);

        assertEquals(1, texts.size());
        assertEquals("Some text", texts.get(0));
    }
}
