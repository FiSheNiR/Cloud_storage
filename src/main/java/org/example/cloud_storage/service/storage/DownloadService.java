package org.example.cloud_storage.service.storage;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.service.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DownloadService {

    private final MinioService minioService;

    public InputStream downloadResource(String path, String username) {
        if (path.endsWith("/")) {
            return downloadDirectory(path, username);
        } else {
            return downloadFile(path, username);
        }
    }

    private InputStream downloadFile(String path, String username) {
        try {
            return minioService.getResource(path, username);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + path, e);
        }
    }

    private InputStream downloadDirectory(String path, String username) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            Iterable<Result<Item>> items = minioService.directoryInfo(path, username, true);

            for (Result<Item> result : items) {
                Item item = result.get();
                String objectName = item.objectName();
                String relativePath = objectName.substring(path.length()).replaceAll("^/+", "");
                if (relativePath.isEmpty() || objectName.equals(path)) {
                    continue;
                }

                ZipEntry zipEntry = new ZipEntry(relativePath);
                zipOut.putNextEntry(zipEntry);

                if (!item.isDir()) {
                    try (InputStream inputStream = minioService.getResource(objectName, username)) {
                        StreamUtils.copy(inputStream, zipOut);
                    }
                }
                zipOut.closeEntry();
            }

            zipOut.finish();
            return new ByteArrayInputStream(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to create ZIP for directory: " + path, e);
        }
    }
}