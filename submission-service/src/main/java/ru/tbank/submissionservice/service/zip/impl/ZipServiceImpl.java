package ru.tbank.submissionservice.service.zip.impl;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.exception.test_case.InvalidTestCasesException;
import ru.tbank.submissionservice.service.zip.ZipService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZipServiceImpl implements ZipService {

    private final ObjectMapper objectMapper;

    @Override
    public byte[] zipTestCases(List<TestCase> testCases) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(bos);

        int counter = 1;
        for (TestCase testCase : testCases) {
            ZipEntry zipEntry = new ZipEntry(String.format("test_case_%d.json", counter++));
            zipOutputStream.putNextEntry(zipEntry);
            byte[] testCaseBytes = objectMapper.writeValueAsBytes(testCase);
            zipOutputStream.write(testCaseBytes);
            zipOutputStream.closeEntry();
        }

        zipOutputStream.finish();
        zipOutputStream.close();

        return bos.toByteArray();
    }

    @Override
    public List<TestCase> unzipTestCases(byte[] zipBytes) throws IOException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes));
        ZipEntry zipEntry = zis.getNextEntry();
        List<TestCase> testCases = new ArrayList<>();

        while (zipEntry != null) {
            byte[] bytes = zis.readAllBytes();
            try {
                TestCase testCase = objectMapper.readValue(bytes, TestCase.class);
                testCases.add(testCase);
            } catch (DatabindException e) {
                throw new InvalidTestCasesException("Invalid test cases");
            }
            zipEntry = zis.getNextEntry();
        }

        return testCases;
    }

}
