package com.solvex.ragflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RagFlowService {

    @Value("${ragflow.api.base-url}")
    private String baseUrl;

    @Value("${ragflow.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendMessage(String message, String conversationId) {
        // Mock implementation for RAGFlow API
        // In real scenario, construct the request payload and call the API
        
        // Mock response
        return "This is a response from RAGFlow for: " + message + ". [Citation: Document A, Page 1]";
    }
    
    public Map<String, Object> streamMessage(String message, String conversationId) {
         // Implement SSE or WebSocket logic here
         // For now returning simple map
         Map<String, Object> response = new HashMap<>();
         response.put("answer", "Processed: " + message);
         return response;
    }
}
