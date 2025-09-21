package org.example.cloud_storage.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class ResourceResponseDto{
    private String path;
    private String name;
    private Long size;
    private String type;
}
