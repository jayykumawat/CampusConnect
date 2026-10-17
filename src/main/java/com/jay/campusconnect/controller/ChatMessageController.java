package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.service.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/events/{eventId}/messages")
    public ResponseEntity<ChatMessageResponse> create(
            @PathVariable Long eventId,
            @Valid @RequestBody ChatMessageCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatMessageService.createMessage(eventId, request));
    }

    @GetMapping("/events/{eventId}/messages")
    public List<ChatMessageResponse> getByEventId(@PathVariable Long eventId) {
        return chatMessageService.getMessagesByEventId(eventId);
    }
}
