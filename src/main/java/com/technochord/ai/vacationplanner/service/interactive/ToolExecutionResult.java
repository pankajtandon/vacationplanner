package com.technochord.ai.vacationplanner.service.interactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;

@Data
@AllArgsConstructor
public class ToolExecutionResult {
    private AssistantMessage.ToolCall toolCall;
    private String result;
    private boolean executed;
}
