package com.innowise.academy.user_service.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequestDto(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 100)
        String surname,

        @NotNull
        @Past
        LocalDate birthDate,

        @NotBlank
        @Email
        String email
) {
}