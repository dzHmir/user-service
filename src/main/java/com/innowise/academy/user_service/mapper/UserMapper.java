package com.innowise.academy.user_service.mapper;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import com.innowise.academy.user_service.dto.response.UserWithCardsResponseDto;
import com.innowise.academy.user_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserRequestDto dto);

    UserResponseDto toDto(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserRequestDto dto, @MappingTarget UserEntity entity);

    UserWithCardsResponseDto toWithCardsDto(UserEntity entity);
}
