package ru.tbank.submissionservice.service;

import ru.tbank.submissionservice.dto.TestCase;

import java.io.IOException;
import java.util.List;

public interface YandexCloudS3Service {
    /**
     * Uploads content of archive with test cases to s3 object storage
     *
     * @param testArchiveContent - content of zip file with test cases
     * @return s3 key to uploaded archive file
     */
    String uploadTestArchive(int problemId, byte[] testArchiveContent);

    /**
     * Downloads test cases and unpack them to list of pairs
     * Archive must contain files input_n.txt and output_n.txt
     *
     * @param problemId - id of the problem to download
     * @return list of pairs (input, output)
     */
    List<TestCase> getTestCases(int problemId) throws IOException;

    /**
     * Uploads user submission code to s3 object storage
     *
     * @param sourceCode - source code of user's submission
     * @return source code s3 key
     */
    String uploadSourceCode(int userId, int problemId, String sourceCode);

    /**
     * Downloads source code from s3 object storage
     *
     * @param userId    - owner of the code
     * @param problemId - the task for which the code was created
     * @return source code
     */
    String downloadSourceCode(int userId, int problemId) throws IOException;

}
