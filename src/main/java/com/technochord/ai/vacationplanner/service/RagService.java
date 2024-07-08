package com.technochord.ai.vacationplanner.service;

import com.technochord.ai.vacationplanner.config.RagCandidateSpringContext;
import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class RagService {
    private RagCandidateSpringContext ragCandidateSpringContext;

    private VectorStore vectorStore;

    private FunctionCallbackContext functionCallbackContext;

    private RagProperties ragProperties;

    public RagService(final RagCandidateSpringContext ragCandidateSpringContext,
                      final VectorStore vectorStore,
                      final FunctionCallbackContext functionCallbackContext,
                      final RagProperties ragProperties) {
        this.ragCandidateSpringContext = ragCandidateSpringContext;
        this.vectorStore = vectorStore;
        this.functionCallbackContext = functionCallbackContext;
        this.ragProperties = ragProperties;
    }

    public Set<String> getRagCandidateFunctionNameSet(final String query) {
        Set<String> ragBeans =  ragCandidateSpringContext.getRagCandidateServiceBeanNames();
        Map<String, String> functionMap = new Hashtable();
        if (ragBeans != null) {
            for (String beanName: ragBeans) {
                FunctionCallback functionCallback = functionCallbackContext.getFunctionCallback(beanName, null);
                functionMap.put(functionCallback.getName(), functionCallback.getDescription());
            }
        }

        Set<String> ragCandidateFunctionNameSet = getTopKFunctionNames(functionMap, Math.min(functionMap.size(), ragProperties.getTopK()), query);

        return ragCandidateFunctionNameSet;
    }


    private Set<String> getTopKFunctionNames(final Map<String, String> functionMap, final int topK, final String query) {

        AtomicInteger vectorizeCount = new AtomicInteger();
        //Add function metadata to vectorStore if not exist
        List<Document> docList = new ArrayList<>();
        if (functionMap != null) {
            functionMap.keySet().stream().forEach(k -> {
                List<Document> retrievedDocList = vectorStore.similaritySearch(SearchRequest.defaults()
                                .withQuery(".")
                                .withTopK(1)
                                .withSimilarityThresholdAll()
                                .withFilterExpression("name == '" + k + "'"));

                if (retrievedDocList == null || retrievedDocList.size() == 0) {
                    //Insert into vectorStore
                    Document doc = new Document(functionMap.get(k), Map.of("name", k));
                    vectorStore.add(List.of(doc));
                    docList.add(doc);
                    vectorizeCount.getAndIncrement();
                }
            });

            log.info("There were {} ragCandidate beans defined in the context out of which {} were vectorized and inserted into the vectorStore",
                    functionMap.size(), vectorizeCount.get());
        }

        //Now retrieve the topK
        List<Document> retrievedTopK = vectorStore.similaritySearch(SearchRequest.defaults()
                .withQuery(query)
                .withTopK(topK)
                .withSimilarityThreshold(ragProperties.similarityThreshold));

        if (retrievedTopK == null || retrievedTopK.size() == 0) {
            log.warn("Found no functions to support query using a similarity threshold of {}. " +
                    "Returing an empty set. Query is: {}", ragProperties.getSimilarityThreshold(), query);
            return Collections.EMPTY_SET;
        } else {
            Float max = Collections.max(retrievedTopK.stream().map(d -> (Float) d.getMetadata().get("distance")).collect(Collectors.toList()));
            Float min = Collections.min(retrievedTopK.stream().map(d -> (Float) d.getMetadata().get("distance")).collect(Collectors.toList()));
            List<String> nameList = retrievedTopK.stream().map(d -> (String) d.getMetadata().get("name")).collect(Collectors.toList());

            log.info("There were {} functions found that were relevant to the passed in query, with a distance range from {} to {}", nameList.size(), min, max);
            return new HashSet<>(nameList);
        }
    }
}
