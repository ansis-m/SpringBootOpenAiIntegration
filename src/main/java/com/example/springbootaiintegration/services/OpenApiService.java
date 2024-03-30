package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.dtos.ExchangeDto;
import com.example.springbootaiintegration.mongoRepos.entities.Model;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
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
        this.model = Model.GPT_3_5_TURBO.getValue();
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
        var options = OpenAiChatOptions.builder()
                                     .withModel(this.model)
                                     .withTemperature(0.4F)
                                     .withMaxTokens(TOKENS)
                                     .withN(1)
                                     .build();
        var combinedClient = new OpenAiChatClient(aiApi, options);
        this.streamingClient = combinedClient;
        this.client = combinedClient;
    }

    @Override
    void addSystemMessage(ExchangeDto exchangeDto) {

    }

    @Override
    void addModel(ExchangeDto exchangeDto) {
        //TODO make this more generic and possible to select
        exchangeDto.setModel(this.model);
    }
}
