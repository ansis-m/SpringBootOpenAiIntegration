package com.example.springbootaiintegration.controllers;

import com.example.springbootaiintegration.services.LlamaApiService;
import com.example.springbootaiintegration.services.OpenApiService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET}, allowCredentials = "true")
public class OpenApiController {


    private final Map<String, SseEmitter> clientEmitters = new ConcurrentHashMap<>();
    private final OpenApiService openApiService;
    private final LlamaApiService llamaApiService;

    @Autowired
    OpenApiController(OpenApiService openApiService,
                      LlamaApiService llamaApiService) {
        this.openApiService = openApiService;
        this.llamaApiService = llamaApiService;
    }

    @PostMapping(value = "/openapi/post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postMessage(@RequestBody Map<String, Object> request, @CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {

        sessionId = manageCookies(sessionId, response);
        return openApiService.getFlux(request, sessionId);
    }


    @PostMapping(value = "/llama/post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postLamaMessage(@RequestBody Map<String, Object> request, @CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {

        sessionId = manageCookies(sessionId, response);
        return llamaApiService.getFlux(request, sessionId);
    }


    //establish the initial connection
    @GetMapping("/connection")
    public SseEmitter stream(@CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sessionId = manageCookies(sessionId, response);
        clientEmitters.put(sessionId, emitter);
        String finalSessionId = sessionId;
        emitter.onCompletion(() -> clientEmitters.remove(finalSessionId));
        emitter.onTimeout(() -> clientEmitters.remove(finalSessionId));
        System.out.println("Connection established. Cookie: " + finalSessionId);
        return emitter;
    }




    @GetMapping(value = "/llama/post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getLamaMessage(@RequestParam String prompt, @RequestParam boolean clearContext, @CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {

        System.out.println("cookie: " + sessionId);
        System.out.println("params: " + prompt + " " + clearContext);

        sessionId = manageCookies(sessionId, response);
        return llamaApiService.getFlux(new HashMap<String, Object>(){{
            put("prompt", prompt);
            put("clearContext", clearContext);
        }}, sessionId);
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
