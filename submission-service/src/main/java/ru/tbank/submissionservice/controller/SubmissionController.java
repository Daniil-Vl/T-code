package ru.tbank.submissionservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;

import java.util.concurrent.ExecutionException;

@RequestMapping("/api/v1/submission")
public interface SubmissionController {

    @PostMapping
    SubmissionToken saveSubmissionResult(
            @RequestBody @Valid SubmissionRequestBody submissionRequestBody
    );

}