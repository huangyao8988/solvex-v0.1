package com.solvex.ragflow.controller;

import com.solvex.ragflow.entity.Conversation;
import com.solvex.ragflow.entity.Message;
import com.solvex.ragflow.entity.User;
import com.solvex.ragflow.repository.ConversationRepository;
import com.solvex.ragflow.repository.MessageRepository;
import com.solvex.ragflow.service.RagFlowService;
import com.solvex.ragflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RagFlowService ragFlowService;
    
    @Autowired
    private UserService userService; // To get user details

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        String content = payload.get("message");
        Long conversationId = payload.containsKey("conversationId") ? Long.parseLong(payload.get("conversationId")) : null;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Conversation conversation;
        if (conversationId == null) {
            conversation = new Conversation();
            conversation.setTitle(content.length() > 20 ? content.substring(0, 20) + "..." : content);
            // In real app, find user by username and set it
            // conversation.setUser(user);
            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository.findById(conversationId).orElseThrow();
        }

        // Save User Message
        Message userMsg = new Message();
        userMsg.setConversation(conversation);
        userMsg.setContent(content);
        userMsg.setRole("user");
        messageRepository.save(userMsg);

        // Call RAGFlow
        String response = ragFlowService.sendMessage(content, conversation.getId().toString());

        // Save Assistant Message
        Message botMsg = new Message();
        botMsg.setConversation(conversation);
        botMsg.setContent(response);
        botMsg.setRole("assistant");
        // Mock citation parsing
        if (response.contains("[Citation:")) {
            botMsg.setCitation("{\"source\": \"Document A\", \"text\": \"Original text...\"}");
        }
        messageRepository.save(botMsg);

        return ResponseEntity.ok(Map.of(
            "response", response,
            "conversationId", conversation.getId(),
            "citation", botMsg.getCitation()
        ));
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        // Return list of conversations for current user
        return ResponseEntity.ok(conversationRepository.findAll());
    }
    
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        Conversation conv = conversationRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(conv.getMessages());
    }
}
