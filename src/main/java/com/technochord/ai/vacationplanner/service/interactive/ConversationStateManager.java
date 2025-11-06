package com.technochord.ai.vacationplanner.service.interactive;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationStateManager {

    private final Map<String, ConversationState> states = new ConcurrentHashMap<>();

    public void saveState(ConversationState state) {
        states.put(state.getConversationId(), state);
        cleanupOldStates();
    }

    public ConversationState getState(String conversationId) {
        return states.get(conversationId);
    }

    public void updateState(ConversationState state) {
        states.put(state.getConversationId(), state);
    }

    public void deleteState(String conversationId) {
        states.remove(conversationId);
    }

    private void cleanupOldStates() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(1));
        states.entrySet().removeIf(entry ->
                entry.getValue().getCreatedAt().isBefore(cutoff));
    }
}
