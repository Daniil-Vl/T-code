package ru.tbank.submissionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.dto.error.ApiErrorResponse;

import java.io.IOException;
import java.util.List;

@Tag(name = "TestCaseController")
public interface TestCaseController {

    @Operation(summary = "Get problem`s test cases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test cases successfully retrieved"),
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
    @GetMapping("/problem/{problem_id}")
    List<TestCase> getTestCases(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId
    ) throws IOException;

    @Operation(summary = "Save tests cases for given problem in object storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test cases successfully saved"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not positive problem id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid test cases",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping("/problem/{problem_id}")
    void saveTestCases(
            @PathVariable("problem_id") @Positive(message = "problem id must be positive") long problemId,
            @RequestBody @Valid List<TestCase> testCases
    ) throws IOException;

}
