package com.technochord.ai.vacationplanner.model.interactive;

import lombok.Data;

@Data
public class PlannerChatRequest {
    private String message;
    private int userSuppliedTopK;
    private String modelName;
    private String temperature;
}
