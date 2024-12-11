package ru.tbank.submissionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "LanguageController")
public interface LanguageController {

    @Operation(summary = "Get available languages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Languages successfully retrieved")
    })
    @GetMapping
    List<String> getLanguages();

}
