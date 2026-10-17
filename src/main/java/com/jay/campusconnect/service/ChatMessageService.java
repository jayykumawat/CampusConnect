package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponse createMessage(Long eventId, ChatMessageCreateRequest request);

    List<ChatMessageResponse> getMessagesByEventId(Long eventId);
}
