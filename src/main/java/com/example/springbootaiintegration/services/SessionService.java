package com.example.springbootaiintegration.services;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {


    private final Map<UUID, List<Message>> sessions = new ConcurrentHashMap<>();
    public List<Message> getConversation(UUID sessionId) {
        return sessions.get(sessionId);
    }

    public void addConversation(UUID id, List<Message> conversation) {
        sessions.put(id, conversation);
    }
}
