package com.example.springbootaiintegration.services;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlamaApiService extends AbstractApiService {

    @Autowired
    LlamaApiService(SessionService sessionService) {
        intro = "You are an expert programmer who gives excellent programming advice. ";
        this.sessionService = sessionService;

        this.client = new OllamaChatClient(new OllamaApi())
                .withDefaultOptions(OllamaOptions.create()
                          //.withModel(OllamaOptions.DEFAULT_MODEL)
                          .withModel("codellama")
                          .withTemperature(0.9f));
    }

}
