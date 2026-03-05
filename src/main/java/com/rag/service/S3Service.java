package com.rag.service;

import com.rag.common.exception.InternalServerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;

@ApplicationScoped
public class S3Service {

    @ConfigProperty(name = "s3.endpoint", defaultValue = "")
    String endpoint;

    @ConfigProperty(name = "s3.region")
    String region;

    @ConfigProperty(name = "s3.access-key")
    String accessKey;

    @ConfigProperty(name = "s3.secret-key")
    String secretKey;

    @ConfigProperty(name = "s3.bucket")
    String bucket;

    private S3Client s3Client;

    @PostConstruct
    void init() {
        var credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        var builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials);

        // In dev punta a LocalStack, in prod usa l'endpoint reale AWS
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true) // necessario per LocalStack
                            .build());
        }

        this.s3Client = builder.build();
    }

    public String upload(InputStream fileStream, String fileName, String mimeType, long fileSize) {
        String key = "documents/" + fileName;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(mimeType)
                            .contentLength(fileSize)
                            .build(),
                    RequestBody.fromInputStream(fileStream, fileSize)
            );
            return key;
        } catch (S3Exception e) {
            throw new InternalServerException("S3 upload failed: " + e.getMessage());
        }
    }

    public InputStream download(String storagePath) {
        try {
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(storagePath)
                            .build()
            );
        } catch (S3Exception e) {
            throw new InternalServerException("S3 download failed: " + e.getMessage());
        }
    }

    public void delete(String storagePath) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(storagePath)
                            .build()
            );
        } catch (S3Exception e) {
            throw new InternalServerException("S3 delete failed: " + e.getMessage());
        }
    }

}