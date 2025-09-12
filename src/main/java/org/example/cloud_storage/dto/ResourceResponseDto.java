package org.example.cloud_storage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceResponseDto{
    private String path;
    private String name;
    private Long size;
    private String type;
}
