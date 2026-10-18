package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.service.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageWebSocketController(
            ChatMessageService chatMessageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/events/{eventId}/messages")
    public void sendMessage(
            @DestinationVariable Long eventId,
            @Valid @Payload ChatMessageCreateRequest request
    ) {
        ChatMessageResponse response = chatMessageService.createMessage(eventId, request);
        messagingTemplate.convertAndSend("/topic/events/" + eventId + "/messages", response);
    }
}
