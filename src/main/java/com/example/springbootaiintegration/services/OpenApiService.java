package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.dtos.MessageDto;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OpenApiService {

    private final String apiKey;
    private final SessionService sessionService;
    private final OpenAiApi aiApi;
    private final OpenAiChatClient client;


    @Autowired
    OpenApiService(@Value("${API_KEY}") String apiKey,
                   SessionService sessionService) {
        this.apiKey = apiKey;
        this.sessionService = sessionService;

        this.aiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));

        this.client = new OpenAiChatClient(aiApi)
                .withDefaultOptions(OpenAiChatOptions.builder()
                                                     .withModel("gpt-3.5-turbo")
                                                     .withTemperature(0.4F)
                                                     .withMaxTokens(250)
                                                     .withN(1)
                                                     .build());
    }


    public Flux<String> getFlux(Map<String, Object> request, String id) {

        var conversation = sessionService.getConversation(id);
        if (conversation == null || request.get("clearContext").equals(Boolean.TRUE)) {
            conversation = new Session(id, new LinkedList<>());
        }

        validate(conversation);
        Message message = new UserMessage((String) request.get("prompt"));
        conversation.getMessages().add(new MessageDto(message));
        AtomicInteger count = new AtomicInteger(0);
        var prompt = new Prompt(conversation.getMessages().stream().map(MessageDto::convert).toList());
        var flux = client.stream(prompt)
                         .map(response ->
                              {
                                    count.incrementAndGet();
                                    var responeString = response.getResult().getOutput().getContent();
                                    if (responeString != null) {
                                        return responeString;
                                    } else {
                                        response.getMetadata().getPromptMetadata().forEach(m -> System.out.println(m.getPromptIndex()));
                                        return "\n\n";
                                    }
                              }
                         ).doOnNext(System.out::print)
                .publish()
                .autoConnect(2);

        appendResponseToConversation(flux, conversation, id);

        return flux;
    }

    private void appendResponseToConversation(Flux<String> flux, Session conversation, String id) {
        flux.collectList().subscribe(
                list -> {
                    var response = new AssistantMessage(String.join("", list));
                    conversation.getMessages().add(new MessageDto(response));
                    sessionService.addConversation(conversation);
                },
                error -> {
                    System.out.println("error collecting the list!");
                });
    }

    private void validate(Session session) {

        System.out.println("\n\n\nid: " + session.getId() + "\n\n\n");

        var conversation = session.getMessages();
        while (conversation.size() != 0 && conversation.get(conversation.size() - 1).getType().equals("user")) {
            conversation.remove(conversation.size() - 1);
        }
    }
}
