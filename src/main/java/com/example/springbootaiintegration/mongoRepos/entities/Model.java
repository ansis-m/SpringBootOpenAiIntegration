package com.example.springbootaiintegration.mongoRepos.entities;

import com.example.springbootaiintegration.services.CustomOllamaOptions;
import org.springframework.ai.openai.api.OpenAiApi;

public enum Model {
    MISTRAL(CustomOllamaOptions.DEFAULT_MODEL),
    CODELLAMA(CustomOllamaOptions.CODELLAMA),
    GPT_4_0125_PREVIEW(OpenAiApi.ChatModel.GPT_4_0125_PREVIEW.value),
    GPT_4_TURBO_PREVIEW(OpenAiApi.ChatModel.GPT_4_0125_PREVIEW.value),
    GPT_4_VISION_PREVIEW(OpenAiApi.ChatModel.GPT_4_VISION_PREVIEW.value),
    GPT_4(OpenAiApi.ChatModel.GPT_4.value),
    GPT_4_32K(OpenAiApi.ChatModel.GPT_4_32K.value),
    GPT_3_5_TURBO(OpenAiApi.ChatModel.GPT_3_5_TURBO.value),
    GPT_3_5_TURBO_0125(OpenAiApi.ChatModel.GPT_3_5_TURBO_0125.value),
    GPT_3_5_TURBO_1106(OpenAiApi.ChatModel.GPT_3_5_TURBO_1106.value);

    private final String value;

    Model(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
