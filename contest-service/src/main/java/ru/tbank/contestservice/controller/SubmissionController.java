package ru.tbank.contestservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tbank.contestservice.dto.error.ApiErrorResponse;
import ru.tbank.contestservice.dto.submission.SubmissionRequest;

import java.util.Set;

@Tag(name = "SubmissionController")
public interface SubmissionController {

    @Operation(summary = "Get available languages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Languages successfully retrieved")
    })
    @GetMapping("/language")
    Set<String> getAvailableLanguages();

    @Operation(summary = "Submit solution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solution successfully submitted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Contest already finished",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not registered for contest",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Problem not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("contest/{contest_id}/problem/{problem_id}/submit")
    void submit(
            @PathVariable("contest_id") @Positive(message = "contest id must be positive") long contestId,
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId,
            @RequestBody @Valid SubmissionRequest submissionRequest
    );

}
