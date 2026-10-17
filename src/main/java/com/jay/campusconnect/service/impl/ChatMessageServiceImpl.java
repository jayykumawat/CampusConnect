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
import com.jay.campusconnect.service.ChatMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageServiceImpl(
            ChatMessageRepository chatMessageRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            ChatMessageMapper chatMessageMapper
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public ChatMessageResponse createMessage(Long eventId, ChatMessageCreateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User sender = userRepository.findById(request.senderUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id " + request.senderUserId()
                ));

        ChatMessage chatMessage = chatMessageMapper.toEntity(request, event, sender);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        return chatMessageRepository.findByEventOrderByTimestampAsc(event).stream()
                .map(chatMessageMapper::toResponse)
                .toList();
    }
}
