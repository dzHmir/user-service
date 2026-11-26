package com.innowise.academy.user_service.mapper;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import com.innowise.academy.user_service.entity.PaymentCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentCardEntity toEntity(PaymentCardRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    PaymentCardResponseDto toDto(PaymentCardEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PaymentCardRequestDto dto, @MappingTarget PaymentCardEntity entity);
}
