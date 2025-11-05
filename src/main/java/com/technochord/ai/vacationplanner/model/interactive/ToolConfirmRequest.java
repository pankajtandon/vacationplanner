package com.technochord.ai.vacationplanner.model.interactive;

import lombok.Data;

@Data
public class ToolConfirmRequest {
    private String conversationId;
    private boolean approved;
    private String feedback;
    private String modelName;
}
