package com.innowise.academy.user_service.dto.response;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        boolean active
) {
}