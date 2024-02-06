package com.example.springbootaiintegration.services;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenApiService extends AbstractApiService{

    @Autowired
    OpenApiService(SessionService sessionService) {
        this.sessionService = sessionService;

        OpenAiApi aiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));

        this.client = new OpenAiChatClient(aiApi)
                .withDefaultOptions(OpenAiChatOptions.builder()
                                                     .withModel("gpt-3.5-turbo")
                                                     .withTemperature(0.4F)
                                                     .withMaxTokens(250)
                                                     .withN(1)
                                                     .build());
    }

}
