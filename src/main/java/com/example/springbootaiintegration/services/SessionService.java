package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.SessionRepository;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository repository;
    private final MongoTemplate template;

    @Autowired
    SessionService(SessionRepository repository, MongoTemplate template){
        this.repository = repository;
        this.template = template;
    }

    public Optional<Session> getSession(String sessionId) {
        return repository.findById(sessionId);
    }

    public Session addSession(Session conversation) {
        return repository.save(conversation);
    }

    public Session saveName(String id, String name) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("name", name);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return template.findAndModify(query, update, options, Session.class);
    }

}
