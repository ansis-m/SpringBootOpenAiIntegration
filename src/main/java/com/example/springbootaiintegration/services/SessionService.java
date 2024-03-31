package com.example.springbootaiintegration.services;

import com.example.springbootaiintegration.mongoRepos.HistoryRepository;
import com.example.springbootaiintegration.mongoRepos.SessionRepository;
import com.example.springbootaiintegration.mongoRepos.entities.History;
import com.example.springbootaiintegration.mongoRepos.entities.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final HistoryRepository historyRepository;
    private final MongoTemplate template;

    @Transactional
    //TODO enable transactional in mongodb
    public void clearSession(String oldCookie, String newCookie,
                             Map<String, SseEmitter> clientEmitters,
                             Map<String, Disposable> disposables) {
        var history = historyRepository.findById(oldCookie).orElse(new History());
        var oldSession = sessionRepository.findById(oldCookie).orElseThrow(() -> new RuntimeException("no session to clear"));
        history.setId(newCookie);
        var emitter = clientEmitters.get(oldCookie);
        clientEmitters.remove(oldCookie);
        clientEmitters.put(newCookie, emitter);
        history.getSessionIdsAndNames().put(oldCookie, oldSession.getName());
        historyRepository.save(history);
        historyRepository.deleteById(oldCookie);

    }

    public Optional<Session> getSession(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public Session addSession(Session conversation) {
        return sessionRepository.save(conversation);
    }

    public Session saveName(String id, String name) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("name", name);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return template.findAndModify(query, update, options, Session.class);
    }

}
