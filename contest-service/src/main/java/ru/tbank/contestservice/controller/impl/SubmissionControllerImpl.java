package ru.tbank.contestservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.controller.SubmissionController;
import ru.tbank.contestservice.dto.submission.SubmissionRequest;
import ru.tbank.contestservice.service.ContestService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submission")
public class SubmissionControllerImpl implements SubmissionController {

    private final ContestService contestService;
    private final SubmissionServiceClient submissionServiceClient;

    @Override
    public Set<String> getAvailableLanguages() {
        return submissionServiceClient.getAvailableLanguages();
    }

    @Override
    public void submit(long contestId, long problemId, SubmissionRequest submissionRequest) {
        contestService.submit(contestId, problemId, submissionRequest);
    }

}
