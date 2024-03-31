package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.dtos.ExchangeDto;
import com.example.springbootaiintegration.mongoRepos.entities.Session;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.Prompt;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractApiService {

    SessionService sessionService;
    ChatClient client;
    StreamingChatClient streamingClient;
    String model = "";


    public Flux<String> getFlux(Map<String, Object> request, String id) {

        var conversation = sessionService.getSession(id).orElse(null);
        if (conversation == null || request.get("clearContext").equals(Boolean.TRUE)) {
            conversation = new Session(id);
        }

        ExchangeDto newExchange = new ExchangeDto((String) request.get("prompt"), MessageType.USER.getValue());
        addSystemMessage(newExchange);
        addModel(newExchange);

        conversation.getExchanges().add(newExchange);

        var prompt = new Prompt(conversation.getExchanges().stream().map(ExchangeDto::convert).flatMap(this::streamAndFilter).toList());

        initializeClient(request);
        Flux<String> flux = streamingClient.stream(prompt)
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

    private Stream<Message> streamAndFilter(List<Message> messages) {
        return messages.stream().filter(message -> !(message.getContent() == null && MessageType.SYSTEM.equals(message.getMessageType())));
    }

    private void appendResponseToConversation(Flux<String> flux, Session conversation) {
        flux.collectList().subscribe(
                list -> {
                    var response = String.join("", list);
                    conversation.getExchanges().getLast().setResponse(response);
                    sessionService.addSession(conversation);
                },
                error -> System.out.println("error collecting the list!"));
    }

    abstract void initializeClient(Map<String, Object> request);
    abstract void makeClient();
    abstract void addSystemMessage(ExchangeDto exchangeDto);
    abstract void addModel(ExchangeDto exchangeDto);
}
