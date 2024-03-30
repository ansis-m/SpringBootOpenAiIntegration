package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.dtos.ExchangeDto;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenApiService extends AbstractApiService{


    private Integer TOKENS = 250;
    private final OpenAiApi aiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));

    @Autowired
    OpenApiService(SessionService sessionService) {
        this.sessionService = sessionService;
        this.model = "gpt-3.5-turbo";
        makeClient();
    }

    @Override
    void initializeClient(Map<String, Object> request) {

        var makeNew = false;
        String requestModel = (String) request.get("model");
        Integer requestTokens = (Integer) request.get("tokens");

        if (requestModel != null && !requestModel.trim().isEmpty() && !this.model.equals(requestModel)) {
            this.model = requestModel.trim();
            makeNew = true;
        }

        if (requestTokens != null && !this.TOKENS.equals(requestTokens)) {
            this.TOKENS = requestTokens;
            makeNew = true;
        }

        if (makeNew) {
            makeClient();
        }
    }

    @Override
    void makeClient() {
        var combinedClient = new OpenAiChatClient(aiApi);
        this.streamingClient = combinedClient;
        this.client = combinedClient;
//                .withDefaultOptions(OpenAiChatOptions.builder()
//                                                     .withModel(this.model)
//                                                     .withTemperature(0.4F)
//                                                     .withMaxTokens(TOKENS)
//                                                     .withN(1)
//                                                     .build());
    }

    @Override
    void addSystemMessage(ExchangeDto exchangeDto) {

    }
}
