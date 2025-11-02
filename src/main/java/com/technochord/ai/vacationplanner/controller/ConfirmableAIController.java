package com.technochord.ai.vacationplanner.controller;

import com.technochord.ai.vacationplanner.service.interactive.ConfirmableToolChatService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/ai")
@Log4j2
public class ConfirmableAIController {

    private final ConfirmableToolChatService chatService;

    public ConfirmableAIController(ConfirmableToolChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String response = chatService.chat(request.getMessage());

            // Check if response contains confirmation request
            if (response.contains("Conversation ID:")) {
                return ResponseEntity.ok(ChatResponse.confirmationNeeded(response));
            }

            return ResponseEntity.ok(ChatResponse.finalResponse(response));

        } catch (Exception e) {
            log.error("Error processing chat", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/confirm-tool")
    public ResponseEntity<ChatResponse> confirmTool(@RequestBody ToolConfirmRequest request) {
        try {
            String response = chatService.confirmTool(
                    request.getConversationId(),
                    request.isApproved(),
                    request.getFeedback()
            );

            // Check if more confirmations needed
            if (response.contains("Conversation ID:")) {
                return ResponseEntity.ok(ChatResponse.confirmationNeeded(response));
            }

            return ResponseEntity.ok(ChatResponse.finalResponse(response));

        } catch (Exception e) {
            log.error("Error confirming tool", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error(e.getMessage()));
        }
    }
}

@Data
class ChatRequest {
    private String message;
}

@Data
class ToolConfirmRequest {
    private String conversationId;
    private boolean approved;
    private String feedback;
}

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
