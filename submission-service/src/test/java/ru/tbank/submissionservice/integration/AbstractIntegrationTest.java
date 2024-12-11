package ru.tbank.submissionservice.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.spring.EnableWireMock;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.util.List;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.enabled=true",
                "spring.main.allow-bean-definition-overriding=true",
                "app.judge0-api-base-url=${wiremock.server.baseUrl}",
                "spring.cache.type=NONE"
        }
)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableWireMock
public class AbstractIntegrationTest {

    protected static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-submission-db")
            .withUsername("test-user")
            .withPassword("test-password")
            .withReuse(true);

    protected static RabbitMQContainer RABBITMQ = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:4.0-management")
    ).withReuse(true);

    protected static LocalStackContainer LOCALSTACK = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.5.0")
    ).withServices(LocalStackContainer.Service.S3).withReuse(true);

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ApplicationConfig config;

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
    static void yandexCloudProperties(DynamicPropertyRegistry registry) {
        LOCALSTACK.start();
        registry.add("app.yandex-cloud.access-key-id", LOCALSTACK::getAccessKey);
        registry.add("app.yandex-cloud.secret-access-key", LOCALSTACK::getSecretKey);
        registry.add("app.yandex-cloud.submission-source-code-bucket-name", () -> "test-submission-source-code");
        registry.add("app.yandex-cloud.test-archive-bucket-name", () -> "test-test-case-archive");
        registry.add("app.yandex-cloud.endpoint", LOCALSTACK::getEndpoint);
        registry.add("app.yandex-cloud.region", LOCALSTACK::getRegion);
    }

    @DynamicPropertySource
    static void rabbitMQProperties(DynamicPropertyRegistry registry) {
        RABBITMQ.start();
        registry.add("spring.rabbitmq.host", RABBITMQ::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBITMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBITMQ::getAdminPassword);

        registry.add("app.rabbit-mq.submission-queue-name", () -> "test-submission-queue");
        registry.add("app.rabbit-mq.submission-dead-letter-queue-name", () -> "test-submission-dead-letter-queue");
        registry.add("app.rabbit-mq.is-durable", () -> "false");
    }

    @AfterEach
    void clearDatabase() {
        submissionRepository.deleteAll();
    }

    @AfterEach
    void clearWireMockStubs() {
        WireMock.reset();
    }

    protected void createBuckets() {
        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(config.yandexCloud().testArchiveBucketName())
                        .build()
        );

        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(config.yandexCloud().submissionSourceCodeBucketName())
                        .build()
        );
    }

    protected void deleteBuckets() {
        List<Bucket> buckets = s3Client.listBuckets().buckets();

        // Clear and delete buckets
        for (var bucket : buckets) {
            ListObjectsResponse listObjectsResponse = s3Client.listObjects(
                    ListObjectsRequest.builder().bucket(bucket.name()).build()
            );

            for (var object : listObjectsResponse.contents()) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder().bucket(bucket.name()).key(object.key()).build()
                );
            }

            s3Client.deleteBucket(
                    DeleteBucketRequest.builder().bucket(bucket.name()).build()
            );
        }
    }
}
