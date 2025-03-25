package ru.ivalykhin.assistant_ai.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIClientConfig {

    @Bean
    public OpenAIClient openAIClient(@Value("${openai.api-key}") String apiKey) {
        return new OpenAIOkHttpClient.Builder().apiKey(apiKey).build();
    }
}
