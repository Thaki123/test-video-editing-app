package com.example.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.api.service.JobService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobWebSocketTest {

    @LocalServerPort
    int port;

    @Autowired
    JwtEncoder encoder;

    @MockBean
    JobService jobService;

    @Test
    void connects() throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + token());
        CompletableFuture<String> future = new CompletableFuture<>();
        client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                future.complete(message.getPayload());
            }
        }, headers, URI.create("ws://localhost:" + port + "/v1/jobs/1/ws"));
        assertEquals("connected", future.get(5, TimeUnit.SECONDS));
    }

    private String token() {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("user").build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
