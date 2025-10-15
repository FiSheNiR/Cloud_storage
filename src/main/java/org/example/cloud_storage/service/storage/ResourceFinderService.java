package org.example.cloud_storage.service.storage;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.mapper.ResponseDtoMapper;
import org.example.cloud_storage.service.MinioService;
import org.example.cloud_storage.util.PathUtil;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceFinderService {

    private final MinioService minioService;

    public List<ResourceResponseDto> getDirectoryInfo(String encodedPath, String username) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        List<ResourceResponseDto> result = new ArrayList<>();
        Iterable<Result<Item>> minioResults = minioService.directoryInfo(path, username, false);
        for (Result<Item> resultItem : minioResults) {
            try {
                Item item = resultItem.get();
                if (item.isDir()) {
                    result.add(ResponseDtoMapper.toDirectoryResponseDto(item.objectName()));
                } else if (item.size() > 1){
                    result.add(getResourceInfo(item.objectName(), item.size()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public ResourceResponseDto getResourceInfo(String encodedPath, Long size) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        return ResponseDtoMapper.toFileResponseDto(path,size);
    }

    public ResourceResponseDto getResourceInfo(String encodedPath, String username) {
        String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        Long size = minioService.fileInfo(path, username);
        return ResponseDtoMapper.toFileResponseDto(path,size);
    }

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
                    results.add(ResponseDtoMapper.toDirectoryResponseDto(objectName));
                } else if (item.size() > 1){
                    results.add(ResponseDtoMapper.toFileResponseDto(objectName, item.size()));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return results;
    }
}
