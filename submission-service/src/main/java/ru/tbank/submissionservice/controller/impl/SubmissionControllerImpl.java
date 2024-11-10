package ru.tbank.submissionservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.submissionservice.controller.SubmissionController;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.service.SubmissionService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SubmissionControllerImpl implements SubmissionController {

    private final SubmissionService submissionService;

    @Override
    public SubmissionToken submit(SubmissionRequestBody submissionRequestBody) {
        return submissionService.submit(
                submissionRequestBody.sourceCode(),
                Language.valueOf(submissionRequestBody.language().toUpperCase()),
                submissionRequestBody.stdin()
        );
    }

    @Override
    public SubmissionResult getSubmissionResult(String submissionToken) {
        return null;
    }

}
