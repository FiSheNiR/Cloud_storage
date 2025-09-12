package org.example.cloud_storage.service;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioService minioService;

    public void deleteResource(String path, String username) {
        minioService.deleteResource(path, username);
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
        Iterable<Result<Item>> minioResults = minioService.directoryInfo(path, username);
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
                } else {
                    result.add(getResourceInfo(item.objectName(), username));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
