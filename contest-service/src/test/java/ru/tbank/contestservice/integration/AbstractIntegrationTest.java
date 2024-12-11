package ru.tbank.contestservice.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;
import ru.tbank.contestservice.dao.jpa.ContestRepository;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dao.jpa.TestCaseRepository;
import ru.tbank.contestservice.dao.jpa.UserRepository;
import ru.tbank.contestservice.model.entities.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.enabled=true",
                "spring.main.allow-bean-definition-overriding=true",
                "app.submission-service-url=${wiremock.server.baseUrl}"
        }
)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableWireMock
public class AbstractIntegrationTest {

    protected static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-contest-db")
            .withUsername("test-user")
            .withPassword("test-password");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void dataSourceInit(DynamicPropertyRegistry registry) {
        POSTGRES.start();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @DynamicPropertySource
    static void liquibaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/master.yml");
    }

    @DynamicPropertySource
    static void rabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("app.rabbit-mq.submission-queue-name", () -> "test-submission-queue");
        registry.add("app.rabbit-mq.submission-dead-letter-queue-name", () -> "test-submission-dead-letter-queue");
        registry.add("app.rabbit-mq.is-durable", () -> "false");

    }

    @AfterEach
    void clearDatabase() {
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        contestRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected UserEntity newUser(String username, String password) {
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(userEntity);
    }

    /**
     * Creates user and return authorization header value for given user
     *
     * @param username
     * @param password
     * @return
     */
    protected String setAuth(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString(
                        String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8)
                );
    }

}
