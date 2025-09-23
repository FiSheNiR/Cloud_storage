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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioService minioService;

    public List<ResourceResponseDto> searchResources(String encodedQuery, String username) {
        String query = URLDecoder.decode(encodedQuery, StandardCharsets.UTF_8);
        List<ResourceResponseDto> results = new ArrayList<>();
        Iterable<Result<Item>> items = minioService.directoryInfo("", username, true);
        for (Result<Item> result : items) {
            try {
                Item item = result.get();
                String objectName = item.objectName();
                System.out.println(objectName);
                String name = PathUtil.getFileName(objectName);
                if (!name.toLowerCase().contains(query.toLowerCase())) {
                    continue;
                }
                if (PathUtil.isDirectory(objectName)) {
                    results.add(directoryResponseDto(objectName));
                } else if (item.size() > 1){
                    results.add(fileResponseDto(objectName, username));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return results;
    }

    public void downloadResource(String path, HttpServletResponse response, String username) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        if (decodedPath.endsWith("/")) {
            downloadDirectory(decodedPath, response, username);
        } else {
            downloadFile(decodedPath, response, username);
        }
    }

    private void downloadFile(String path, HttpServletResponse response, String username) {
        try {
            InputStream inputStream = minioService.getResource(path, username);
            response.setContentType("application/octet-stream");
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadDirectory(String path, HttpServletResponse response, String username) {

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

    public ResourceResponseDto moveResource(String sourcePath, String targetPath, String username) {
        if (PathUtil.isDirectory(sourcePath)) {
            return moveDirectory(sourcePath, targetPath, username);
        } else {
            return moveFile(sourcePath, targetPath, username);
        }
    }

    private ResourceResponseDto moveFile(String sourcePath, String targetPath, String username) {
        minioService.copyObject(sourcePath, targetPath, username);
        minioService.deleteResource(sourcePath, username);
        return fileResponseDto(targetPath, username);
    }

    private ResourceResponseDto moveDirectory(String sourcePath, String targetPath, String username) {
        Iterable<Result<Item>> minioResults = minioService.directoryInfo(sourcePath, username, true);
        for (Result<Item> result : minioResults) {
            try {
                Item item = result.get();
                String objectName = item.objectName();
                String newObjectName = targetPath + PathUtil.getFileName(objectName);
                minioService.copyObject(objectName, newObjectName, username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        deleteResource(sourcePath, username);
        return directoryResponseDto(targetPath);
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
        Set<String> createdDirs = new HashSet<>();
        try {
            for (MultipartFile file : files) {
                InputStream inputStream = file.getInputStream();
                String objectName = path + file.getOriginalFilename();
                createParentDirs(objectName,username,createdDirs);
                minioService.uploadResource(objectName, username, inputStream, file.getContentType(), file.getSize());
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

    private void createParentDirs(String fullPath, String username, Set<String> createdDirs) {
        if (!fullPath.contains("/")) {
            return;
        }
        int lastSlashIndex = fullPath.lastIndexOf('/');
        String dirPath = fullPath.substring(0, lastSlashIndex + 1);
        if (createdDirs.contains(dirPath)) {
            return;
        }
        String parentPath = PathUtil.getParentPath(dirPath);
        if (!parentPath.isEmpty() && !parentPath.equals(dirPath)) {
            createParentDirs(parentPath, username, createdDirs);
        }
        minioService.createDirectory(dirPath, username);
        createdDirs.add(dirPath);
    }

    public ResourceResponseDto createDirectory(String path, String username) {
        minioService.createDirectory(path, username);
        return directoryResponseDto(path);
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

    private ResourceResponseDto fileResponseDto(String path, String username) {
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        Long size = minioService.fileInfo(path, username);
        return ResourceResponseDto.builder()
                .name(fileName)
                .size(size)
                .path(parentPath)
                .type("FILE")
                .build();
    }

    private ResourceResponseDto directoryResponseDto(String path) {
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        return ResourceResponseDto.builder()
                .name(fileName+"/")
                .path(parentPath)
                .type("DIRECTORY")
                .build();
    }
}
