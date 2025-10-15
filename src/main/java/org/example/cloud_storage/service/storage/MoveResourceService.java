package org.example.cloud_storage.service.storage;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.mapper.ResponseDtoMapper;
import org.example.cloud_storage.service.MinioService;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoveResourceService {

    private final MinioService minioService;
    private final ResourceFinderService resourceFinderService;
    private final DeleteResourceService deleteResourceService;

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
        return resourceFinderService.getResourceInfo(targetPath, username);
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
        deleteResourceService.deleteResource(sourcePath, username);
        return ResponseDtoMapper.toDirectoryResponseDto(targetPath);
    }
}
