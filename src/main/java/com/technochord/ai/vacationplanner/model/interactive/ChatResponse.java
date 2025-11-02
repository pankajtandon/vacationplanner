package com.technochord.ai.vacationplanner.model.interactive;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ChatResponse {
    private String response;
    private boolean needsConfirmation;
    private String conversationId;
    private String error;

    public static ChatResponse confirmationNeeded(String response) {
        String convId = extractConversationId(response);
        return ChatResponse.builder()
                .response(response)
                .needsConfirmation(true)
                .conversationId(convId)
                .build();
    }

    public static ChatResponse finalResponse(String response) {
        return ChatResponse.builder()
                .response(response)
                .needsConfirmation(false)
                .build();
    }

    public static ChatResponse error(String error) {
        return ChatResponse.builder()
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
