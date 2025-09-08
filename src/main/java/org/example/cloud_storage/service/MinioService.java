package org.example.cloud_storage.service;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    public void createUserBucket(String userName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(userName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
