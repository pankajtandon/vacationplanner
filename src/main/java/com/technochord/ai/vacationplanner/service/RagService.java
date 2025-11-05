package com.technochord.ai.vacationplanner.service;

import com.technochord.ai.vacationplanner.config.RagCandidateSpringContext;
import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.AsyncMcpToolCallback;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
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

    private List<ToolCallback> availableToolCallbackList;


    private RagProperties ragProperties;

    public RagService(final RagCandidateSpringContext ragCandidateSpringContext,
                      final VectorStore vectorStore,
                      final List<ToolCallback> availableToolCallbackList,
                      final RagProperties ragProperties) {
        this.ragCandidateSpringContext = ragCandidateSpringContext;
        this.vectorStore = vectorStore;
        this.availableToolCallbackList = availableToolCallbackList;
        this.ragProperties = ragProperties;
    }

    public Set<String> getRagCandidateFunctionNameSet(final String query, final Integer userSuppliedTopK) {
        List<ToolCallback> compositeToolCallbackList = new ArrayList<>();

        //First get the RagCandidate Tools from the all the available tools
        Set<String> ragBeans =  ragCandidateSpringContext.getRagCandidateServiceBeanNames();
        if (availableToolCallbackList != null) {
            //First get only those methodToolCallbacks that are RagCandidates
            List<ToolCallback> methodTcList = availableToolCallbackList.stream()
                    .filter(tc -> tc instanceof MethodToolCallback)
                    .filter(tc -> ragBeans.contains(tc.getToolDefinition().name())).toList();
            //Then add all the mcp tools anyway because they are not and cannot be annotated with @RagCandidate
            List<ToolCallback> mcpTcList = availableToolCallbackList.stream().filter(tc ->
                    (tc instanceof SyncMcpToolCallback || tc instanceof AsyncMcpToolCallback)).toList();
            compositeToolCallbackList.addAll(methodTcList);
            compositeToolCallbackList.addAll(mcpTcList);
        }
        Map<String, String> functionMap = new Hashtable();
        if (compositeToolCallbackList != null) {
            for (ToolCallback tc: compositeToolCallbackList) {
                functionMap.put(tc.getToolDefinition().name(), tc.getToolDefinition().description());
            }
        }


        //Now get the most relevant tools for the query from both the method and MCP tool set.
        Set<String> ragCandidateFunctionNameSet = getTopKFunctionNames(functionMap, Math.min(functionMap.size(),
                ((userSuppliedTopK == null || userSuppliedTopK == 0) ?  ragProperties.getTopK() : userSuppliedTopK)), query);

        log.info("Found a total of {} ragCandidates!", ragCandidateFunctionNameSet.size());
        return ragCandidateFunctionNameSet;
    }


    private Set<String> getTopKFunctionNames(final Map<String, String> functionMap, final int topK, final String query) {

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        AtomicInteger vectorizeCount = new AtomicInteger();
        //Add function metadata to vectorStore if not exist
        if (functionMap != null) {
            functionMap.keySet().stream().forEach(k -> {
                List<Document> retrievedDocList = vectorStore.similaritySearch(SearchRequest.builder()
                                .query(functionMap.get(k))
                                .filterExpression(b.eq("name", k).build()).build());
                if (ragProperties.isDeletePreviousRelatedEmbeddings() && retrievedDocList != null) {
                    //Delete this embedding
                    vectorStore.delete(retrievedDocList.stream().map(d -> d.getId()).toList());
                    retrievedDocList = null;
                }
                if (retrievedDocList == null || retrievedDocList.size() == 0) {
                    //Insert into vectorStore
                    Document doc = new Document(functionMap.get(k), Map.of("name", k));
                    vectorStore.add(List.of(doc));
                    vectorizeCount.getAndIncrement();
                }
            });

            log.info("There were {} ragCandidate beans defined in the context out of which {} were " +
                            "vectorized and inserted into the vectorStore " +
                            (vectorizeCount.get() < functionMap.size() ? "(possibly because they already existed in vector store. " +
                                    "If you would like to re-embed these RagCandidates, then set rag.delete-previous-related-embeddings to true)"
                                    : "(Possibly because this is the first time these embeddings are being stored " +
                                    "or because rag.delete-previous-related-embeddings is set to true.)"),
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
