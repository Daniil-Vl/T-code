package ru.tbank.submissionservice.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionToken;

@RequestMapping("/api/v1/submission")
public interface SubmissionController {

    @PostMapping
    SubmissionToken saveSubmissionResult(
            @RequestBody @Valid SubmissionRequestBody submissionRequestBody
    );

}
