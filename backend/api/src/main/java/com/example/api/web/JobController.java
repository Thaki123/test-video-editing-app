package com.example.api.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.service.JobService;

@RestController
@RequestMapping("/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public Map<String, String> create(@RequestBody JobRequest request) {
        String id = jobService.enqueueJob(request);
        return Map.of("id", id);
    }

    @GetMapping("/{id}")
    public JobStatusResponse status(@PathVariable String id) {
        return jobService.getJob(id);
    }
}
