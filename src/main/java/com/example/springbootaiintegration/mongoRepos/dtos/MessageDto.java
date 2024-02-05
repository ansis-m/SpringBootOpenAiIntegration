package com.example.springbootaiintegration.mongoRepos.dtos;

import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

@Data
public class MessageDto {
    private String content;
    private String type;

    public MessageDto() {
    }

    public MessageDto(Message message) {
        this.content = message.getContent();
        this.type = message.getMessageType().getValue();
    }

    public MessageDto(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public MessageDto(String response) {
        this.content = response;
        this.type = MessageType.ASSISTANT.getValue();
    }

    public Message convert() {
        if (type.equals(MessageType.USER.getValue())) {
            return new UserMessage(this.content);
        }
        return new AssistantMessage(this.content);
    }
}