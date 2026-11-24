package com.innowise.academy.user_service.dto.response;

import java.time.LocalDate;
import java.util.List;

public record UserWithCardsResponseDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        boolean active,
        List<PaymentCardResponseDto> cards
) {
}
