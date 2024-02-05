package com.example.springbootaiintegration.controllers;

import com.example.springbootaiintegration.services.OpenApiService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
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
    public Flux<String> postMessage(@RequestBody Map<String, Object> request, @CookieValue(name = "sessionId", required = false) String sessionId, HttpServletResponse response) {

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();

        }
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return openApiService.getFlux(request, sessionId);
    }
}
