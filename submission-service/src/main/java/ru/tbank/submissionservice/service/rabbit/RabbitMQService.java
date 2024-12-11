package ru.tbank.submissionservice.service.rabbit;

import ru.tbank.submissionservice.dto.submission.SubmissionMessage;

import java.io.IOException;

public interface RabbitMQService {

    /**
     * Process submission message, received from submission queue
     *
     * @param message
     * @throws IOException
     */
    void processSubmissionMessage(SubmissionMessage message) throws IOException;


}
