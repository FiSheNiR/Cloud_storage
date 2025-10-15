package org.example.cloud_storage.service.storage;

import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.service.MinioService;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final MinioService minioService;

    public List<ResourceResponseDto> uploadResource(String encodedPath, List<MultipartFile> files, String username) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
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
}
