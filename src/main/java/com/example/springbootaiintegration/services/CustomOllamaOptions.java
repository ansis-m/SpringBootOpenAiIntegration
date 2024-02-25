package com.example.springbootaiintegration.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.ai.ollama.api.OllamaOptions;

@JsonInclude(Include.NON_NULL)
public class CustomOllamaOptions extends org.springframework.ai.ollama.api.OllamaOptions {
    public static final String DEFAULT_MODEL = "mistral";
    public static final String CODELLAMA = "codellama";


    public static OllamaOptions createCodellamaOptions(String model) {
        return new OllamaOptions().withModel(model).withTemperature(0.9f);
    }

}
