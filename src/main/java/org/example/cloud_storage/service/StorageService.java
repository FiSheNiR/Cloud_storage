package org.example.cloud_storage.service;

import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.mapper.ResponseDtoMapper;
import org.example.cloud_storage.service.storage.*;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final ResourceFinderService resourceFinderService;
    private final MinioService minioService;
    private final UploadService uploadService;
    private final DeleteResourceService deleteResourceService;
    private final MoveResourceService moveResourceService;
    private final DownloadService downloadService;

    public List<ResourceResponseDto> searchResources(String encodedQuery, String username) {
        return resourceFinderService.searchResources(encodedQuery, username);
    }

    public InputStream downloadResource(String encodedPath, String username) {
        try {
            String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
            return downloadService.downloadResource(path, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceResponseDto moveResource(String sourcePath, String targetPath, String username) {
        return moveResourceService.moveResource(sourcePath, targetPath, username);
    }

    public void deleteResource(String path, String username) {
        deleteResourceService.deleteResource(path, username);
    }

    public List<ResourceResponseDto> uploadResource(String path, List<MultipartFile> files, String username) {
        return uploadService.uploadResource(path, files, username);
    }



    public ResourceResponseDto createDirectory(String path, String username) {
        minioService.createDirectory(path, username);
        return ResponseDtoMapper.toDirectoryResponseDto(path);
    }

    public ResourceResponseDto getResourceInfo(String encodedPath, String username) {
        return resourceFinderService.getResourceInfo(encodedPath, username);
    }

    public List<ResourceResponseDto> getDirectoryInfo(String encodedPath, String username) {
        return resourceFinderService.getDirectoryInfo(encodedPath, username);
    }

}
