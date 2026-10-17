package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.ChatMessageCreateRequest;
import com.jay.campusconnect.dto.response.ChatMessageResponse;
import com.jay.campusconnect.service.ChatMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatMessageControllerTest {

    private final ChatMessageService chatMessageService = mock(ChatMessageService.class);
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatMessageController(chatMessageService))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.destroy();
    }

    @Test
    void createReturns201WithSafeResponseFields() throws Exception {
        ChatMessageResponse response = new ChatMessageResponse(
                3L, 7L, 12L, "Asha", "Hello", LocalDateTime.of(2025, 1, 1, 10, 0)
        );
        when(chatMessageService.createMessage(7L, new ChatMessageCreateRequest("Hello", 12L)))
                .thenReturn(response);

        mockMvc.perform(post("/api/events/7/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"messageText\":\"Hello\",\"senderUserId\":12}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.eventId").value(7))
                .andExpect(jsonPath("$.senderUserId").value(12))
                .andExpect(jsonPath("$.senderName").value("Asha"))
                .andExpect(jsonPath("$.messageText").value("Hello"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void createReturns400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/events/7/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"messageText\":\" \",\"senderUserId\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMessagesReturns200WithHistory() throws Exception {
        List<ChatMessageResponse> responses = List.of(
                new ChatMessageResponse(1L, 7L, 12L, "Asha", "First",
                        LocalDateTime.of(2025, 1, 1, 10, 0)),
                new ChatMessageResponse(2L, 7L, 12L, "Asha", "Second",
                        LocalDateTime.of(2025, 1, 1, 11, 0))
        );
        when(chatMessageService.getMessagesByEventId(7L)).thenReturn(responses);

        mockMvc.perform(get("/api/events/7/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageText").value("First"))
                .andExpect(jsonPath("$[1].messageText").value("Second"));
    }
}
