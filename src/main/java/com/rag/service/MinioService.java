package com.rag.service;

import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.UUID;

@ApplicationScoped
public class MinioService {

    @ConfigProperty(name = "minio.url")
    String minioUrl;

    @ConfigProperty(name = "minio.access-key")
    String accessKey;

    @ConfigProperty(name = "minio.secret-key")
    String secretKey;

    @ConfigProperty(name = "minio.bucket")
    String bucket;

    private MinioClient client() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    // Uploads a file to MinIO and returns the storage path
    public String upload(InputStream stream, String fileName, String mimeType, long fileSize) {
        try {
            MinioClient minioClient = client();

            // Create bucket if it does not exist
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            // Generate unique object name to avoid collisions
            String objectName = UUID.randomUUID() + "-" + fileName;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(stream, fileSize, -1)
                    .contentType(mimeType)
                    .build());

            return bucket + "/" + objectName;

        } catch (MinioException e) {
            throw new RuntimeException("Failed to upload file to MinIO: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during MinIO upload", e);
        }
    }

    // Downloads a file from MinIO and returns its InputStream
    public InputStream download(String storagePath) {
        try {
            // storagePath format: bucket/objectName
            String objectName = storagePath.substring(storagePath.indexOf("/") + 1);

            return client().getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());

        } catch (MinioException e) {
            throw new RuntimeException("Failed to download file from MinIO: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during MinIO download", e);
        }
    }

    // Deletes a file from MinIO
    public void delete(String storagePath) {
        try {
            String objectName = storagePath.substring(storagePath.indexOf("/") + 1);

            client().removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());

        } catch (MinioException e) {
            throw new RuntimeException("Failed to delete file from MinIO: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during MinIO delete", e);
        }
    }

}