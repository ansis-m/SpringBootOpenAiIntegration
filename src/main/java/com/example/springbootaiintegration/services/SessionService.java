package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.SessionRepository;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository repository;

    @Autowired
    SessionService(SessionRepository repository){
        this.repository = repository;
    }

    public Optional<Session> getConversation(String sessionId) {
        return repository.findById(sessionId);
    }

    public void addConversation(Session conversation) {
        repository.save(conversation);
    }

}
