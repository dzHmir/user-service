package com.innowise.academy.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

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