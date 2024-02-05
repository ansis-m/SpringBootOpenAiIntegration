package com.example.springbootaiintegration.mongoRepos.dtos;

import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
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

    public Message convert() {
        if (type.equals("user")) {
            return new UserMessage(this.content);
        }
        return new AssistantMessage(this.content);
    }
}