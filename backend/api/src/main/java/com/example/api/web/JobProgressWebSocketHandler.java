package com.example.api.web;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.api.service.JobService;

@Component
public class JobProgressWebSocketHandler extends TextWebSocketHandler {
    private final JobService jobService;

    public JobProgressWebSocketHandler(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Could register session for updates
        session.sendMessage(new TextMessage("connected"));
    }
}
