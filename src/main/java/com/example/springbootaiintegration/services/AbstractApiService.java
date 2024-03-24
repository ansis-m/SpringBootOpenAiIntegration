package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.dtos.MessageDto;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.Prompt;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;

public abstract class AbstractApiService {

    SessionService sessionService;
    StreamingChatClient client;
    String intro = "";
    String model = "";


    public Flux<String> getFlux(Map<String, Object> request, String id) {

        var conversation = sessionService.getConversation(id);
        if (conversation == null || request.get("clearContext").equals(Boolean.TRUE)) {
            conversation = new Session(id, new LinkedList<>());
        }

        validate(conversation);

        conversation.getMessages().add(new MessageDto(intro + request.get("prompt"), MessageType.USER.getValue()));
        var prompt = new Prompt(conversation.getMessages().stream().map(MessageDto::convert).toList());

        initializeClient(request);
        var flux = client.stream(prompt)
                         .map(response ->
                              {
                                  var responeString = response.getResult().getOutput().getContent();
                                  if (responeString != null) {
                                  } else {
                                      response.getMetadata().getPromptMetadata().forEach(m -> System.out.println(m.getPromptIndex()));
                                      return "\n\n";
                                  }
                                  return responeString;
                              }
                         ).doOnNext(System.out::print)
                         .publish()
                         .autoConnect(2);

        appendResponseToConversation(flux, conversation);

        return flux
                .map(string -> string.replace(" ", "\u00A0"))
                .concatWith(Mono.just("STREAM_END"));
    }

    private void appendResponseToConversation(Flux<String> flux, Session conversation) {
        flux.collectList().subscribe(
                list -> {
                    var response = String.join("", list);
                    conversation.getMessages().add(new MessageDto(response));
                    sessionService.addConversation(conversation);
                },
                error -> System.out.println("error collecting the list!"));
    }

    private static void validate(Session session) {

        var conversation = session.getMessages();
        while (conversation.size() != 0 && conversation.get(conversation.size() - 1).getType().equals("user")) {
            conversation.remove(conversation.size() - 1);
        }
    }

    abstract void initializeClient(Map<String, Object> request);
    abstract void makeClient();
}
