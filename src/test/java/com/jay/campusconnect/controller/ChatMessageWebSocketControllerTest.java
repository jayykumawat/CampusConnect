package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.service.ChatMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatMessageWebSocketControllerTest {

    private final ChatMessageService chatMessageService = mock(ChatMessageService.class);
    private final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    private final ChatMessageWebSocketController controller =
            new ChatMessageWebSocketController(chatMessageService, messagingTemplate);

    @Test
    void persistsThroughServiceThenBroadcastsResponseToEventTopic() {
        Long eventId = 7L;
        ChatMessageCreateRequest request = new ChatMessageCreateRequest("Hello", 12L);
        ChatMessageResponse persistedResponse = new ChatMessageResponse(
                3L, eventId, 12L, "Asha", "Hello", LocalDateTime.of(2025, 1, 1, 10, 0)
        );
        when(chatMessageService.createMessage(eventId, request)).thenReturn(persistedResponse);

        controller.sendMessage(eventId, request);

        verify(chatMessageService).createMessage(eventId, request);
        verify(messagingTemplate).convertAndSend(
                "/topic/events/7/messages",
                persistedResponse
        );
    }

    @Test
    void doesNotBroadcastWhenPersistenceFails() {
        Long eventId = 7L;
        ChatMessageCreateRequest request = new ChatMessageCreateRequest("Hello", 12L);
        when(chatMessageService.createMessage(eventId, request))
                .thenThrow(new IllegalStateException("Persistence failed"));

        assertThrows(IllegalStateException.class, () -> controller.sendMessage(eventId, request));

        verify(messagingTemplate, never()).convertAndSend(
                eq("/topic/events/7/messages"),
                any(ChatMessageResponse.class)
        );
    }
}
