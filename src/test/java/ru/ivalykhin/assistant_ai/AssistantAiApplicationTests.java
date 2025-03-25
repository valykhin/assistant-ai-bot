package ru.ivalykhin.assistant_ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.ivalykhin.assistant_ai.bot.TelegramBot;

@SpringBootTest
class AssistantAiApplicationTests {
    @MockitoBean
    private TelegramBot telegramBot;

    @Test
    void contextLoads() {
    }

}
