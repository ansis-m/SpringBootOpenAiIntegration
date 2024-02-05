package com.example.springbootaiintegration.controllers;

import com.example.springbootaiintegration.services.OpenApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@RestController
public class OpenApiController {

    private final OpenApiService openApiService;

    @Autowired
    OpenApiController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    @PostMapping(value = "/openapi/post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postMessage(@RequestBody Map<String, Object> request, HttpSession session) {

        var sessionId = session.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID();
            session.setAttribute("sessionId", sessionId);
        }

        return openApiService.getFlux(request, (UUID) sessionId);
    }
}
