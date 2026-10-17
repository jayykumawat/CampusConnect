package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.entity.ChatMessage;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.ChatMessageMapper;
import com.jay.campusconnect.repository.ChatMessageRepository;
import com.jay.campusconnect.repository.EventRepository;
import com.jay.campusconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    private ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void setUp() {
        chatMessageService = new ChatMessageServiceImpl(
                chatMessageRepository,
                eventRepository,
                userRepository,
                new ChatMessageMapper()
        );
    }

    @Test
    void createMessageResolvesEntitiesPersistsAndMapsResponse() {
        Long eventId = 7L;
        Long senderId = 12L;
        ChatMessageCreateRequest request = new ChatMessageCreateRequest("Hello", senderId);
        Event event = org.mockito.Mockito.mock(Event.class);
        User sender = org.mockito.Mockito.mock(User.class);
        when(event.getId()).thenReturn(eventId);
        when(sender.getId()).thenReturn(senderId);
        when(sender.getName()).thenReturn("Asha");
        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ChatMessageResponse response = new ChatMessageResponse(
                null, eventId, senderId, "Asha", "Hello", null
        );
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));

        assertEquals(response, chatMessageService.createMessage(eventId, request));

        org.mockito.ArgumentCaptor<ChatMessage> savedMessage =
                org.mockito.ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(savedMessage.capture());
        assertEquals(event, savedMessage.getValue().getEvent());
        assertEquals(sender, savedMessage.getValue().getSender());
        assertEquals("Hello", savedMessage.getValue().getMessageText());
    }

    @Test
    void createMessageThrowsWhenEventDoesNotExist() {
        Long eventId = 7L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                chatMessageService.createMessage(eventId, new ChatMessageCreateRequest("Hello", 12L))
        );
    }

    @Test
    void createMessageThrowsWhenSenderDoesNotExist() {
        Long eventId = 7L;
        Long senderId = 12L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));
        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                chatMessageService.createMessage(eventId, new ChatMessageCreateRequest("Hello", senderId))
        );
    }

    @Test
    void getMessagesUsesRepositoryTimestampAscendingQueryAndMapsInReturnedOrder() {
        Long eventId = 7L;
        Event event = org.mockito.Mockito.mock(Event.class);
        User sender = org.mockito.Mockito.mock(User.class);
        when(event.getId()).thenReturn(eventId);
        when(sender.getId()).thenReturn(12L);
        when(sender.getName()).thenReturn("Asha");
        ChatMessage earlier = new ChatMessage();
        earlier.setEvent(event);
        earlier.setSender(sender);
        earlier.setMessageText("First");
        ChatMessage later = new ChatMessage();
        later.setEvent(event);
        later.setSender(sender);
        later.setMessageText("Second");
        ChatMessageResponse earlierResponse = new ChatMessageResponse(
                null, eventId, 12L, "Asha", "First", null
        );
        ChatMessageResponse laterResponse = new ChatMessageResponse(
                null, eventId, 12L, "Asha", "Second", null
        );
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(chatMessageRepository.findByEventOrderByTimestampAsc(event)).thenReturn(List.of(earlier, later));

        assertEquals(List.of(earlierResponse, laterResponse),
                chatMessageService.getMessagesByEventId(eventId));
        verify(chatMessageRepository).findByEventOrderByTimestampAsc(event);
    }
}
