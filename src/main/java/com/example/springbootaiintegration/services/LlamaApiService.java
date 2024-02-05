package com.example.springbootaiintegration.services;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class LlamaApiService {


    private final SessionService sessionService;
    private final OllamaApi ollamaApi = new OllamaApi();;
    private final OllamaChatClient client;


    @Autowired
    LlamaApiService(SessionService sessionService) {
        this.sessionService = sessionService;

        this.client = new OllamaChatClient(ollamaApi)
                .withDefaultOptions(OllamaOptions.create()
                          .withModel(OllamaOptions.DEFAULT_MODEL)
                          .withTemperature(0.9f));
    }

    public Flux<String> getFlux(Map<String, Object> request, String sessionId) {

        return client.stream(
                new Prompt("Generate the names of 5 famous pirates.")).map(response -> response.getResult().getOutput().getContent());
    }
}
