package com.example.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

import com.example.api.service.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtEncoder encoder;

    @MockBean
    StorageService storageService;

    @Test
    void initReturnsUrl() throws Exception {
        when(storageService.generatePresignedUrl("file.mp4")).thenReturn("http://example.com");
        mockMvc.perform(post("/v1/uploads/init")
                .header("Authorization", "Bearer " + token())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"filename\":\"file.mp4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://example.com"));
    }

    private String token() {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("user").build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
