package ru.tbank.submissionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tbank.submissionservice.dto.contest.ContestResult;
import ru.tbank.submissionservice.dto.contest.UserRating;
import ru.tbank.submissionservice.dto.error.ApiErrorResponse;

@Tag(name = "ContestController")
public interface ContestController {

    @Operation(summary = "Get contest results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest result successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not positive contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/contest/{contest_id}/result")
    ContestResult getContestResult(
            @PathVariable("contest_id") @Positive(message = "contest id must be positive") long contestId
    );

    @Operation(summary = "Get user`s rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not positive contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/contest/{contest_id}/rating")
    UserRating getUserRating(
            @PathVariable("contest_id") @Positive(message = "contest id must be positive") long contestId
    );

}
