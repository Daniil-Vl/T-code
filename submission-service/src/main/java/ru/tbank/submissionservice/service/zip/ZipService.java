package ru.tbank.submissionservice.service.zip;

import ru.tbank.submissionservice.dto.test_case.TestCase;

import java.io.IOException;
import java.util.List;

public interface ZipService {

    /**
     * Compress test cases into zip archive, which store files test_case_n.json like this
     * {input: ..., output: ...}
     *
     * @param testCases - test cases to zip
     * @return bytes of zip archive
     * @throws IOException
     */
    byte[] zipTestCases(List<TestCase> testCases) throws IOException;

    /**
     * Retrieves test cases from zip archive and sorted them, based on numbers in filenames
     *
     * @param zipBytes - bytes of zip archive
     * @return - unzipped test cases
     * @throws IOException
     */
    List<TestCase> unzipTestCases(byte[] zipBytes) throws IOException;

}
