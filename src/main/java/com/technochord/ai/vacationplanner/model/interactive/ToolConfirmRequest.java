package com.technochord.ai.vacationplanner.model.interactive;

import lombok.Data;

@Data
class ToolConfirmRequest {
    private String conversationId;
    private boolean approved;
    private String feedback;
}
