package com.example.api.web;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.service.StorageService;

@RestController
@RequestMapping("/v1/uploads")
public class UploadController {
    private final StorageService storageService;

    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/init")
    public Map<String, String> init(@RequestBody UploadRequest request) {
        String url = storageService.generatePresignedUrl(request.filename());
        return Map.of("url", url);
    }
}
