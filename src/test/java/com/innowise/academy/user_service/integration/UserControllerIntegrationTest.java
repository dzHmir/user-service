package com.innowise.academy.user_service.integration;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserWithCardsResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUser_andPersistToDatabase() {
        UserRequestDto requestDto = new UserRequestDto(
                "Ivan", "Petrov", LocalDate.of(1990, 5, 10), "ivan@example.com");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/users", requestDto, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void shouldReturnUser_whenUserExists() {
        UserRequestDto requestDto = new UserRequestDto(
                "Anna", "Smith", LocalDate.of(1995, 3, 15), "anna@example.com");

        ResponseEntity<UserWithCardsResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users", requestDto, UserWithCardsResponseDto.class);

        Long createdId = createResponse.getBody().id();

        ResponseEntity<UserWithCardsResponseDto> getResponse = restTemplate.getForEntity(
                "/api/users/" + createdId, UserWithCardsResponseDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Anna", getResponse.getBody().name());
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExist() {
        ResponseEntity<Object> response = restTemplate.getForEntity("/api/users/999999", Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldUpdateUser() {
        UserRequestDto createDto = new UserRequestDto(
                "Oleg", "Original", LocalDate.of(1988, 1, 1), "oleg@example.com");
        ResponseEntity<UserWithCardsResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users", createDto, UserWithCardsResponseDto.class);
        Long createdId = createResponse.getBody().id();

        UserRequestDto updateDto = new UserRequestDto(
                "Oleg", "Updated", LocalDate.of(1988, 1, 1), "oleg@example.com");
        restTemplate.put("/api/users/" + createdId, updateDto);

        ResponseEntity<UserWithCardsResponseDto> getResponse = restTemplate.getForEntity(
                "/api/users/" + createdId, UserWithCardsResponseDto.class);

        assertEquals("Updated", getResponse.getBody().surname());
    }

    @Test
    void shouldDeactivateUser() {
        UserRequestDto createDto = new UserRequestDto(
                "Kate", "Status", LocalDate.of(1992, 6, 20), "kate@example.com");
        ResponseEntity<UserWithCardsResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users", createDto, UserWithCardsResponseDto.class);
        Long createdId = createResponse.getBody().id();

        restTemplate.exchange(
                "/api/users/" + createdId + "/status?active=false",
                HttpMethod.PATCH,
                null,
                UserWithCardsResponseDto.class);

        ResponseEntity<UserWithCardsResponseDto> getResponse = restTemplate.getForEntity(
                "/api/users/" + createdId, UserWithCardsResponseDto.class);

        assertEquals(false, getResponse.getBody().active());
    }

    @Test
    void shouldDeleteUser() {
        UserRequestDto createDto = new UserRequestDto(
                "Boris", "ToDelete", LocalDate.of(1985, 4, 12), "boris@example.com");
        ResponseEntity<UserWithCardsResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users", createDto, UserWithCardsResponseDto.class);
        Long createdId = createResponse.getBody().id();

        restTemplate.delete("/api/users/" + createdId);

        ResponseEntity<Object> getResponse = restTemplate.getForEntity("/api/users/" + createdId, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not-an-email", "missing-at-sign.com", "ivan@"})
    void shouldReturnBadRequest_whenEmailInvalid(String invalidEmail) {
        UserRequestDto requestDto = new UserRequestDto(
                "Ivan", "Petrov", LocalDate.of(1990, 5, 10), invalidEmail);

        ResponseEntity<Object> response = restTemplate.postForEntity("/api/users", requestDto, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
