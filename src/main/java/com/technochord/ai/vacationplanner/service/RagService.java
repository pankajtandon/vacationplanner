package com.technochord.ai.vacationplanner.service;

import com.technochord.ai.vacationplanner.config.RagCandidateSpringContext;
import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class RagService {
    private RagCandidateSpringContext ragCandidateSpringContext;

    private VectorStore vectorStore;

    private ToolCallbackResolver toolCallbackResolver;

    private RagProperties ragProperties;

    public RagService(final RagCandidateSpringContext ragCandidateSpringContext,
                      final VectorStore vectorStore,
                      final ToolCallbackResolver toolCallbackResolver,
                      final RagProperties ragProperties) {
        this.ragCandidateSpringContext = ragCandidateSpringContext;
        this.vectorStore = vectorStore;
        this.toolCallbackResolver = toolCallbackResolver;
        this.ragProperties = ragProperties;
    }

    public Set<String> getRagCandidateFunctionNameSet(final String query, final Integer userSuppliedTopK) {
        Set<String> ragBeans =  ragCandidateSpringContext.getRagCandidateServiceBeanNames();
        Map<String, String> functionMap = new Hashtable();
        if (ragBeans != null) {
            for (String beanName: ragBeans) {
                FunctionToolCallback functionCallback = (FunctionToolCallback) toolCallbackResolver.resolve(beanName);
                functionMap.put(functionCallback.getToolDefinition().name(), functionCallback.getToolDefinition().description());
            }
        }

        Set<String> ragCandidateFunctionNameSet = getTopKFunctionNames(functionMap, Math.min(functionMap.size(),
                ((userSuppliedTopK == null || userSuppliedTopK == 0) ?  ragProperties.getTopK() : userSuppliedTopK)), query);

        return ragCandidateFunctionNameSet;
    }


    private Set<String> getTopKFunctionNames(final Map<String, String> functionMap, final int topK, final String query) {

        //During testing, delete everything
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        vectorStore.delete(b.eq("a", "a").build());

        AtomicInteger vectorizeCount = new AtomicInteger();
        //Add function metadata to vectorStore if not exist
        if (functionMap != null) {
            functionMap.keySet().stream().forEach(k -> {
                List<Document> retrievedDocList = vectorStore.similaritySearch(SearchRequest.builder()
                                .query(".")
                                .topK(1)
                                .similarityThresholdAll()
                                .filterExpression("name == '" + k + "'").build());

                //Temp
                vectorStore.delete(retrievedDocList.stream().map(d -> d.getId()).toList());
                retrievedDocList = vectorStore.similaritySearch(SearchRequest.builder()
                        .query(".")
                        .topK(1)
                        .similarityThresholdAll()
                        .filterExpression("name == '" + k + "'").build());

                if (retrievedDocList == null || retrievedDocList.size() == 0) {
                    //Insert into vectorStore
                    Document doc = new Document(functionMap.get(k), Map.of("name", k));
                    vectorStore.add(List.of(doc));
                    vectorizeCount.getAndIncrement();
                }
            });

            log.info("There were {} ragCandidate beans defined in the context out of which {} were " +
                            "vectorized and inserted into the vectorStore " +
                            (vectorizeCount.get() < functionMap.size() ? "(possibly because they already existed in vector store)" : ""),
                    functionMap.size(), vectorizeCount.get());
        }

        //Now retrieve the topK
        List<Document> retrievedTopK = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(ragProperties.similarityThreshold).build());

        if (retrievedTopK == null || retrievedTopK.size() == 0) {
            log.warn("Found no functions to support query using a similarity threshold of {}. " +
                    "Returing an empty set. Query is: {}", ragProperties.getSimilarityThreshold(), query);
            return Collections.EMPTY_SET;
        } else {
            Float max = Collections.max(retrievedTopK.stream().map(d -> (Float) d.getMetadata().get("distance")).collect(Collectors.toList()));
            Float min = Collections.min(retrievedTopK.stream().map(d -> (Float) d.getMetadata().get("distance")).collect(Collectors.toList()));
            List<String> nameList = retrievedTopK.stream().map(d -> (String) d.getMetadata().get("name")).collect(Collectors.toList());

            log.info("There were {} functions found that were relevant to the passed in query, with a distance range from {} to {}", nameList.size(), min, max);
            log.debug("Functions metadata being sent to LLM in descending order of relevance: " + nameList.toString());
            return new HashSet<>(nameList);
        }
    }
}
