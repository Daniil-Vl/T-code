package ru.tbank.contestservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.error.ApiErrorResponse;
import ru.tbank.contestservice.dto.problem.ProblemDTO;

import java.util.List;

@Tag(name = "ProblemController")
public interface ProblemController {

    @Operation(summary = "Create problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Problem successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid problem data",
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
            )
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createProblem(
            @RequestBody @Valid ProblemDTO problemDTO
    );

    @Operation(summary = "Get problem (with test cases)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not positive problem id",
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
                    description = "User not problem owner",
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
    @GetMapping("/{problem_id}")
    ProblemDTO getProblemById(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId
    );

    @Operation(summary = "Get problem description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem successfully retrieved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not positive problem id",
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
    @GetMapping("/{problem_id}/description")
    ProblemDTO getProblemDescriptionById(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId
    );

    @Operation(summary = "Update problem description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem successfully updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid problem data",
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
                    description = "User not problem owner",
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
    @PatchMapping("/{problem_id}/description")
    void updateProblemDescription(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId,
            @RequestBody @NotBlank(message = "description cannot be blank") String description
    );

    @Operation(summary = "Update problem`s test cases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem`s test cases successfully updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid problem data",
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
                    description = "User not problem owner",
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
    @PutMapping("/{problem_id}/test_cases")
    void updateProblemTestCases(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId,
            @RequestBody @NotNull(message = "test cases cannot be null") List<TestCase> testCases
    );

}
