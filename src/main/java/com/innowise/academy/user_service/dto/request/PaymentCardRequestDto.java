package com.innowise.academy.user_service.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PaymentCardRequestDto(

        @NotBlank
        @Size(min = 13, max = 19)
        String number,

        @NotBlank
        @Size(max = 100)
        String holder,

        @NotNull
        @Future
        LocalDate expirationDate
) {
}