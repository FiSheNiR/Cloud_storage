package org.example.cloud_storage.mapper;

import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);
    UserResponseDto toResponseDto(User user);
    UserResponseDto fromRequestToResponseDto(UserRequestDto userRequestDto);

}
