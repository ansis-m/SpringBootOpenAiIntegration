package com.example.springbootaiintegration.services;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Map;

@Service
public class ApiService {


    ApiService(@Value("${API_KEY}") String apiKey) {
        System.out.println("\n\n\n" + apiKey + "\n\n\n");

        var openAiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));

        var chatClient = new OpenAiChatClient(openAiApi)
                .withDefaultOptions(OpenAiChatOptions.builder()
                                                     .withModel("gpt-3.5-turbo")
                                                     .withTemperature(0.4F)
                                                     .withMaxTokens(250)
                                                     .withN(1)
                                                     .build());


        Message m1 = new UserMessage("Which is the best programming language for enterprise web apps?");

        ChatResponse response = chatClient.call(
                new Prompt(m1));

        System.out.print(response.getResult().getOutput().getContent());

        AssistantMessage r1 = response.getResult().getOutput();

        Message m2 = new UserMessage("Please elaborate 2.!");

        response = chatClient.call(new Prompt(Arrays.asList(m1, r1, m2)));


        System.out.print("\n\n\nSecond response:" + response.getResult().getOutput().getContent());

    }


}
