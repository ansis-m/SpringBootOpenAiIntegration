package com.example.springbootaiintegration.services;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LlamaApiService extends AbstractApiService {

    final OllamaOptions options = CustomOllamaOptions.createCodellamaOptions(CustomOllamaOptions.CODELLAMA);


    @Autowired
    LlamaApiService(SessionService sessionService) {
        model = CustomOllamaOptions.CODELLAMA;
        intro = "You are an expert programmer who gives excellent programming advice. ";
        this.sessionService = sessionService;
        makeClient();
    }

    @Override
    void initializeClient(Map<String, Object> request) {

        String requestModel = (String) request.get("model");
        if (requestModel != null && !requestModel.trim().isEmpty() && !this.model.equals(requestModel)) {
            this.model = requestModel.trim();
            makeClient();
        }
    }

    @Override
    void makeClient() {
        this.client = new OllamaChatClient(new OllamaApi())
                .withDefaultOptions(options);
    }
}
