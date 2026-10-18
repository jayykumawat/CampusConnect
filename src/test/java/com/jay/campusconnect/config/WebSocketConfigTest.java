package com.jay.campusconnect.config;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketConfigTest {

    private final WebSocketConfig config = new WebSocketConfig();

    @Test
    void configuresApplicationAndBrokerPrefixes() {
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        config.configureMessageBroker(registry);

        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void registersWebSocketStompEndpoint() {
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);

        config.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws");
    }
}
