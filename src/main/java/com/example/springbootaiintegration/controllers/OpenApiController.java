package com.example.springbootaiintegration.controllers;

import com.example.springbootaiintegration.mongoRepos.entities.Session;
import com.example.springbootaiintegration.services.LlamaApiService;
import com.example.springbootaiintegration.services.OpenApiService;
import com.example.springbootaiintegration.services.SessionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true")
public class OpenApiController {


    private final Map<String, SseEmitter> clientEmitters = new ConcurrentHashMap<>();
    private final Map<String, Disposable> disposables = new ConcurrentHashMap<>();
    private final OpenApiService openApiService;
    private final LlamaApiService llamaApiService;
    private final SessionService sessionService;

    @Autowired
    OpenApiController(OpenApiService openApiService,
                      LlamaApiService llamaApiService,
                      SessionService sessionService) {
        this.openApiService = openApiService;
        this.llamaApiService = llamaApiService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "/openapi/post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postMessage(@RequestBody Map<String, Object> request, @CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {

        sessionId = manageCookies(sessionId, response);
        return openApiService.getFlux(request, sessionId);
    }

    //TODO make a single generic endpoint, all we need is a service selector/factory
    @PostMapping(value = "/llama/post")
    public ResponseEntity<Void> postLamaMessage(@RequestBody Map<String, Object> request, @CookieValue(name = "sessionId") String sessionId) {

        SseEmitter emitter = clientEmitters.get(sessionId);
        try {
            Disposable disposable = llamaApiService.getFlux(request, sessionId).subscribe(data -> {
                try {
                    emitter.send(SseEmitter.event().data(data));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }, error -> {}, () -> {disposables.remove(sessionId);});
            disposables.put(sessionId, disposable);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/terminate")
    public ResponseEntity<Void> terminate(@CookieValue(name = "sessionId") String sessionId){
        try {
            Disposable disposable = disposables.get(sessionId);
            SseEmitter emitter = clientEmitters.get(sessionId);
            emitter.send("STREAM_END");
            disposable.dispose();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //establish the initial connection
    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sessionId = manageCookies(sessionId, response);
        clientEmitters.put(sessionId, emitter);
        String finalSessionId = sessionId;
        emitter.onCompletion(() -> clientEmitters.remove(finalSessionId));
        emitter.onTimeout(() -> clientEmitters.remove(finalSessionId));
        System.out.println("Connection established. Cookie: " + finalSessionId);
        try {
            emitter.send(""); //need to send cookie for the post
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emitter;
    }


    //load the session
    @GetMapping(value = "/load")
    public ResponseEntity<Session> load(@CookieValue(name = "sessionId") String sessionId) {
        return ResponseEntity.of(sessionService.getSession(sessionId));
    }

    @PostMapping(value = "/save")
    public ResponseEntity<String> save(@CookieValue(name = "sessionId") String sessionId, @RequestBody String name) {
        var session = sessionService.saveName(sessionId, name);
        return ResponseEntity.of(Optional.of(session.getName()));
    }

    private String manageCookies(String sessionId, HttpServletResponse response) {
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        return sessionId;
    }
}
