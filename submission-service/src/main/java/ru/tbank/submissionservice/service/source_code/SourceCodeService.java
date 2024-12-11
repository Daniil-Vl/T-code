package ru.tbank.submissionservice.service.source_code;

import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.io.IOException;

public interface SourceCodeService {

    /**
     * Uploads user submission code to s3 object storage
     *
     * @param sourceCode - source code of user's submission
     * @return source code s3 key
     */
    String uploadSourceCode(SubmissionId submissionId, String sourceCode);

    /**
     * Downloads source code from s3 object storage
     *
     * @return source code
     */
    String downloadSourceCode(SubmissionId submissionId) throws IOException;

}
