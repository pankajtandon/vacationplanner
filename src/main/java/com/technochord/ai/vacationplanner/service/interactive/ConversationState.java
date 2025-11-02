package com.technochord.ai.vacationplanner.service.interactive;

import lombok.Data;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.time.Instant;
import java.util.List;

@Data
public class ConversationState {
    private String conversationId;
    private ChatClientRequest originalRequest;
    private ChatClientResponse originalResponse;
    private List<AssistantMessage.ToolCall> toolCalls;
    private int currentToolIndex;
    private List<ToolExecutionResult> toolResults;
    private boolean pendingConfirmation;
    private Instant createdAt = Instant.now();
}
