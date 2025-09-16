package org.example.cloud_storage.service;

import io.minio.Result;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioService minioService;

    public void downloadFile(String path, HttpServletResponse response, String username) {
        try {
            InputStream inputStream = minioService.getResource(path, username);
            response.setContentType("application/octet-stream");
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadDirectory(String path, HttpServletResponse response, String username) {

        response.setContentType("application/zip");

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            Iterable<Result<Item>> items = minioService.directoryInfo(path, username, true);

            for (Result<Item> result : items) {
                Item item = result.get();
                String objectName = item.objectName();
                String relativePath = objectName.substring(path.length()).replaceAll("^/+", "");

                if (relativePath.isEmpty() || objectName.equals(path)) {
                    continue;
                }

                ZipEntry resourceEntry = new ZipEntry(relativePath);

                if (item.isDir()) {
                    zipOut.putNextEntry(resourceEntry);
                    zipOut.closeEntry();
                } else {
                    InputStream inputStream = minioService.getResource(objectName, username);
                    zipOut.putNextEntry(resourceEntry);
                    StreamUtils.copy(inputStream, zipOut);
                    zipOut.closeEntry();
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void deleteResource(String path, String username) {
        if (path.endsWith("/")) {
            Iterable<Result<Item>> items = minioService.directoryInfo(path, username, false);
            for (Result<Item> resultItem : items) {
                try {
                    Item item = resultItem.get();
                    String objectName = item.objectName();
                    if (objectName.equals(path)) {
                        continue;
                    }
                    deleteResource(objectName, username);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            minioService.deleteResource(path, username);
        } else {
            minioService.deleteResource(path, username);
        }
    }

    public List<ResourceResponseDto> uploadResource(String path, List<MultipartFile> files, String username) {
        List<ResourceResponseDto> uploadedResources = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                InputStream inputStream = file.getInputStream();
                String filePath = path + file.getOriginalFilename();
                minioService.uploadResource(filePath, username, inputStream, file.getContentType(), file.getSize());
                uploadedResources.add(ResourceResponseDto.builder()
                        .name(file.getOriginalFilename())
                        .size(file.getSize())
                        .path(path)
                        .type("FILE")
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uploadedResources;
    }

    public ResourceResponseDto createDirectory(String path, String username) {
        minioService.createDirectory(path, username);
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        return ResourceResponseDto.builder()
                .name(fileName+"/")
                .path(parentPath)
                .type("DIRECTORY")
                .build();
    }

    public ResourceResponseDto getResourceInfo(String encodedPath, String username) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        Long size = minioService.fileInfo(path, username);
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        return ResourceResponseDto.builder()
                .name(fileName)
                .size(size)
                .path(parentPath)
                .type("FILE")
                .build();
    }

    public List<ResourceResponseDto> getDirectoryInfo(String encodedPath, String username) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        List<ResourceResponseDto> result = new ArrayList<>();
        Iterable<Result<Item>> minioResults = minioService.directoryInfo(path, username, false);
        for (Result<Item> resultItem : minioResults) {
            try {
                Item item = resultItem.get();
                String parentPath = PathUtil.getParentPath(item.objectName());
                String fileName = PathUtil.getFileName(item.objectName());
                if (item.isDir()) {
                    result.add(ResourceResponseDto.builder()
                            .name(fileName+"/")
                            .path(parentPath)
                            .type("DIRECTORY")
                            .build());
                } else if (item.size() > 1){
                    result.add(getResourceInfo(item.objectName(), username));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }


}
