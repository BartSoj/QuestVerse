package com.example.questverse.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OpenAiConfig {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

}
