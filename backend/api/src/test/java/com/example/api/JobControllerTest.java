package com.example.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.example.api.service.JobService;
import com.example.api.web.JobRequest;
import com.example.api.web.JobStatusResponse;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtEncoder encoder;

    @MockBean
    JobService jobService;

    @Test
    void createReturnsId() throws Exception {
        when(jobService.enqueueJob(any(JobRequest.class))).thenReturn("123");
        mockMvc.perform(post("/v1/jobs")
                .header("Authorization", "Bearer " + token())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uploadKey\":\"k\",\"style\":\"s\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void statusReturnsJob() throws Exception {
        when(jobService.getJob("123")).thenReturn(new JobStatusResponse("123", "DONE", "url"));
        mockMvc.perform(get("/v1/jobs/123")
                .header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.downloadUrl").value("url"));
    }

    private String token() {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("user").build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
