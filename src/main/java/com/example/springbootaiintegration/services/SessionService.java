package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.SessionRepository;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final SessionRepository repository;

    @Autowired
    SessionService(SessionRepository repository){
        this.repository = repository;
    }

    public Session getConversation(String sessionId) {
        return repository.findById(sessionId).orElse(null);
    }

    public void addConversation(Session conversation) {
        repository.save(conversation);
    }

}
