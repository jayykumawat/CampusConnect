package com.jay.campusconnect.repository;


import com.jay.campusconnect.entity.ChatMessage;
import com.jay.campusconnect.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage>findByEventOrderByTimestampAsc(Event event);
}
