package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.entity.ChatMessage;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toEntity(ChatMessageCreateRequest request, Event event, User sender) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setEvent(event);
        chatMessage.setSender(sender);
        chatMessage.setMessageText(request.messageText());
        return chatMessage;
    }

    public ChatMessageResponse toResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getEvent().getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getName(),
                chatMessage.getMessageText(),
                chatMessage.getTimestamp()
        );
    }
}
