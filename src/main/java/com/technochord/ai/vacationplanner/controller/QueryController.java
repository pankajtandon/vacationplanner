package com.technochord.ai.vacationplanner.controller;

import com.technochord.ai.vacationplanner.service.VacationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private VacationService vacationService;

    @GetMapping
    public ResponseEntity<String> getResponse(@RequestBody Query query) {
        return new ResponseEntity<>(vacationService.planVacation(query.getUserQuery(), query.getUserSuppliedTopKFunctions()), HttpStatus.OK);
    }

    @Getter
    @Setter

    /**
     * This class represents the payload that is sent to the app.
     * The userQuery may or may not use the functions that are implemented by this Spring application context.
     * In the case the user anticipates that none of the implmented functions will be relevant to query posed,
     * a low (or 0) value can be passed for `userSuppliedTopKFunctions` resulting in no function metadata being sent
     * to the LLM.
     *
     * If the `userSuppliedTopKFunctions` is not specified in the query payload, the value defined in
     * `rag.topK` property will be used.
     */
    public static class Query {

        /**
         * The query sent to the LLM.
         */
        private String userQuery;

        /**
         * This is an integer representing the top K functions that will be passed to the LLM.
         * The higher the number, the more function metadata will be sent to the LLM, resulting in
         * more tokens being consumed.
         */
        private int userSuppliedTopKFunctions;
    }
}
