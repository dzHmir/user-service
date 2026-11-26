package com.innowise.academy.user_service.dto.response;

import java.time.LocalDate;

public record PaymentCardResponseDto(
        Long id,
        String number,
        String holder,
        LocalDate expirationDate,
        boolean active,
        Long userId
) {
}