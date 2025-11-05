package com.technochord.ai.vacationplanner.model.interactive;

import io.micrometer.common.util.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;

@Data
@Builder
public class PlannerChatResponse {
    private String response;
    private boolean needsConfirmation;
    private String conversationId;
    private String error;
    private List<String> relevantToolList;
    private List<AssistantMessage.ToolCall> toolCallList;

    public static PlannerChatResponse buildResponse(String response, List<AssistantMessage.ToolCall> toolCallList, List<String> relevantToolNameList) {
        String convId = extractConversationId(response);
        return PlannerChatResponse.builder()
                .response(response)
                .needsConfirmation((StringUtils.isEmpty(convId) ? false : true))
                .conversationId(convId)
                .toolCallList(toolCallList)
                .relevantToolList(relevantToolNameList)
                .build();
    }

    public static PlannerChatResponse error(String error) {
        return PlannerChatResponse.builder()
                .error(error)
                .build();
    }

    private static String extractConversationId(String response) {
        if (response.contains("[Conversation ID:")) {
            int start = response.indexOf("[Conversation ID:") + 17;
            int end = response.indexOf("]", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        return null;
    }
}
