package org.example.cloud_storage.mapper;

import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.util.PathUtil;

public class ResponseDtoMapper {

    public static ResourceResponseDto toFileResponseDto(String path, Long size) {
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        return ResourceResponseDto.builder()
                .name(fileName)
                .size(size)
                .path(parentPath)
                .type("FILE")
                .build();
    }

    public static ResourceResponseDto toDirectoryResponseDto(String path) {
        String parentPath = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);
        return ResourceResponseDto.builder()
                .name(fileName+"/")
                .path(parentPath)
                .type("DIRECTORY")
                .build();
    }
}
