package com.example.springbootaiintegration.services;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiService {


    ApiService(@Value("${API_KEY}") String apiKey) {
        System.out.println("\n\n\n" + apiKey + "\n\n\n");

        var openAiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));

        var chatClient = new OpenAiChatClient(openAiApi)
                .withDefaultOptions(OpenAiChatOptions.builder()
                                                     .withModel("gpt-3.5-turbo")
                                                     .withTemperature(0.4F)
                                                     .withMaxTokens(200)
                                                     .build());

        ChatResponse response = chatClient.call(
                new Prompt("What is circumference of earth in kilometers?"));

        System.out.println("\n\n\nResponse: " + response.getResult().getOutput().getContent());

    }


}
