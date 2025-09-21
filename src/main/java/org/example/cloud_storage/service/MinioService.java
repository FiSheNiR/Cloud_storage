package org.example.cloud_storage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public Long fileInfo(String path, String userName){
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(userName).object(path).build());
            return stat.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Result<Item>> directoryInfo(String path, String userName, boolean recursive){
        try {
            return minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(userName)
                    .prefix(path)
                    .recursive(recursive)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getResource(String path, String userName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(userName).object(path).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteResource(String path, String userName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(userName).object(path).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createDirectory(String path, String userName) {
        ByteArrayInputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(userName)
                    .object(path)
                    .stream(emptyInputStream, 0, -1)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void copyObject(String sourcePath, String targetPath, String userName) {
        try {
            CopySource copySource = CopySource.builder().bucket(userName).object(sourcePath).build();

            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(userName)
                    .object(targetPath)
                    .source(copySource)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadResource(String path, String userName, InputStream inputStream, String contentType, long size) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(userName)
                    .object(path)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
