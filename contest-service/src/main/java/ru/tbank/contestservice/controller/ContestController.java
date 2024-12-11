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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.dto.contest.AddProblemContestRequest;
import ru.tbank.contestservice.dto.contest.ContestDTO;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.contest.UserRating;
import ru.tbank.contestservice.dto.error.ApiErrorResponse;
import ru.tbank.contestservice.dto.problem.ProblemDTO;

import java.util.List;


@Tag(name = "ContestController")
public interface ContestController {

    @Operation(summary = "Get contest info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest info successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{contest_id}")
    ContestDTO getContestInfo(
            @PathVariable(name = "contest_id") @Positive(message = "contest id must be positive") long contestId
    );

    @Operation(summary = "Get contest problems")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest problems successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{contest_id}/problems")
    List<ProblemDTO> getProblems(
            @PathVariable(name = "contest_id") @Positive(message = "contest id must be positive") long contestId
    );

    @Operation(summary = "Get contest results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{contest_id}/results")
    ContestResult getContestResult(
            @PathVariable(name = "contest_id") @Positive(message = "contest id must be positive") long contestId
    );

    @Operation(summary = "Get rating of all contest`s participants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{contest_id}/rating")
    UserRating getUserRating(
            @PathVariable("contest_id") @Positive(message = "contest id must be positive") long contestId
    );

    @Operation(summary = "Create contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contest successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createContest(
            @RequestBody @Valid CreateContestRequest createContestRequest
    );

    @Operation(summary = "Add problem to contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem successfully added"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
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
                    description = "Unauthenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not contest`s owner",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/problem")
    void addProblem(
            @RequestBody @Valid AddProblemContestRequest addProblemContestRequest
    );

    @Operation(summary = "Register for contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid contest id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Registration already finished",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contest not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/{contest_id}/register")
    void registerForContest(
            @PathVariable("contest_id") @Positive(message = "contest id must be positive") long contestId
    );

}
