package com.technochord.ai.vacationplanner.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "rag")
public class RagProperties {
    public int topK;
    public double similarityThreshold;
    public boolean deletePreviousRelatedEmbeddings;
}
