package com.example.springbootaiintegration.mongoRepos.dtos;

import lombok.Data;
import org.springframework.ai.chat.messages.*;

import static org.springframework.ai.chat.messages.MessageType.ASSISTANT;
import static org.springframework.ai.chat.messages.MessageType.SYSTEM;
import static org.springframework.ai.chat.messages.MessageType.USER;

import java.util.List;
import java.util.UUID;


@Data
public class ExchangeDto {
    private String request;
    private String response;
    private String systemMessage;

    private String model;
    private String id;

    public ExchangeDto() {
    }

    public ExchangeDto(Message message) {
        initId();
        switch (message.getMessageType()) {
            case USER -> {
                this.request = message.getContent();
            }
            case ASSISTANT -> {
                this.response = message.getContent();
            }
            case SYSTEM -> {
                this.systemMessage = message.getContent();
            }
            case FUNCTION -> {
            }
        }
    }

    public ExchangeDto(String content, String type) {
        initId();
        if(type.equals(USER.getValue())) {
            this.request = content;
        } else if (type.equals(ASSISTANT.getValue())) {
            this.response = content;
        } else if (type.equals(SYSTEM.getValue())) {
            this.systemMessage = content;
        }
    }

    public ExchangeDto(String response) {
        initId();
        this.response = response;
    }

    public List<Message> convert() {
        return List.of(new UserMessage(request), new SystemMessage(systemMessage), new AssistantMessage(response));
    }

    private void initId() {
        this.id = UUID.randomUUID().toString();
    }
}