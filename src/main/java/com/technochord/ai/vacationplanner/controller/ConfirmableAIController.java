package com.technochord.ai.vacationplanner.controller;

import com.technochord.ai.vacationplanner.model.interactive.PlannerChatRequest;
import com.technochord.ai.vacationplanner.model.interactive.PlannerChatResponse;
import com.technochord.ai.vacationplanner.model.interactive.ToolConfirmRequest;
import com.technochord.ai.vacationplanner.service.interactive.ConfirmableToolChatService;
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
    public ResponseEntity<PlannerChatResponse> chat(@RequestBody PlannerChatRequest request) {
        try {
            PlannerChatResponse chatResponse = chatService.chat(request.getMessage(), request.getUserSuppliedTopK(), request.getModelName());
            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            log.error("Error processing chat", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PlannerChatResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/confirm-tool")
    public ResponseEntity<PlannerChatResponse> confirmTool(@RequestBody ToolConfirmRequest request) {
        try {
            String response = chatService.confirmTool(
                    request.getConversationId(),
                    request.isApproved(),
                    request.getFeedback(),
                    request.getModelName()
            );

            return ResponseEntity.ok(PlannerChatResponse.buildResponse(response, null, null));

        } catch (Exception e) {
            log.error("Error confirming tool", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PlannerChatResponse.error(e.getMessage()));
        }
    }
}

