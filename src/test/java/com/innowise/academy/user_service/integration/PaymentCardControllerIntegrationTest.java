package com.innowise.academy.user_service.integration;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import com.innowise.academy.user_service.dto.response.UserWithCardsResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentCardControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long createUser(String name, String surname, String email) {
        UserRequestDto userDto = new UserRequestDto(name, surname, LocalDate.of(1990, 1, 1), email);
        ResponseEntity<UserWithCardsResponseDto> response = restTemplate.postForEntity(
                "/api/users", userDto, UserWithCardsResponseDto.class);
        return response.getBody().id();
    }

    @Test
    void shouldCreateCard_forExistingUser() {
        Long userId = createUser("Ivan", "Petrov", "ivan.cards@example.com");

        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1));

        ResponseEntity<PaymentCardResponseDto> response = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userId, response.getBody().userId());
    }

    @Test
    void shouldReturnNotFound_whenCreatingCardForMissingUser() {
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000001", "Ghost", LocalDate.of(2030, 1, 1));

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/users/999999/cards", cardDto, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnCard_whenCardExists() {
        Long userId = createUser("Anna", "Smith", "anna.cards@example.com");
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000002", "Anna Smith", LocalDate.of(2030, 1, 1));
        ResponseEntity<PaymentCardResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
        Long cardId = createResponse.getBody().id();

        ResponseEntity<PaymentCardResponseDto> getResponse = restTemplate.getForEntity(
                "/api/cards/" + cardId, PaymentCardResponseDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("4000000000000002", getResponse.getBody().number());
    }

    @Test
    void shouldUpdateCard() {
        Long userId = createUser("Oleg", "Card", "oleg.cards@example.com");
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000003", "Oleg Card", LocalDate.of(2030, 1, 1));
        ResponseEntity<PaymentCardResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
        Long cardId = createResponse.getBody().id();

        PaymentCardRequestDto updateDto = new PaymentCardRequestDto(
                "4000000000009999", "Updated Holder", LocalDate.of(2031, 1, 1));
        restTemplate.put("/api/cards/" + cardId, updateDto);

        ResponseEntity<PaymentCardResponseDto> getResponse = restTemplate.getForEntity(
                "/api/cards/" + cardId, PaymentCardResponseDto.class);

        assertEquals("Updated Holder", getResponse.getBody().holder());
    }

    @Test
    void shouldDeactivateCard() {
        Long userId = createUser("Kate", "Card", "kate.cards@example.com");
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000004", "Kate Card", LocalDate.of(2030, 1, 1));
        ResponseEntity<PaymentCardResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
        Long cardId = createResponse.getBody().id();

        restTemplate.exchange(
                "/api/cards/" + cardId + "/status?active=false",
                HttpMethod.PUT,
                null,
                PaymentCardResponseDto.class);

        ResponseEntity<PaymentCardResponseDto> getResponse = restTemplate.getForEntity(
                "/api/cards/" + cardId, PaymentCardResponseDto.class);

        assertEquals(false, getResponse.getBody().active());
    }

    @Test
    void shouldDeleteCard() {
        Long userId = createUser("Boris", "Card", "boris.cards@example.com");
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000005", "Boris Card", LocalDate.of(2030, 1, 1));
        ResponseEntity<PaymentCardResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
        Long cardId = createResponse.getBody().id();

        restTemplate.delete("/api/cards/" + cardId);

        ResponseEntity<Object> getResponse = restTemplate.getForEntity("/api/cards/" + cardId, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void shouldReturnConflict_whenExceedingMaxActiveCards() {
        Long userId = createUser("Limit", "Tester", "limit.tester@example.com");

        for (int i = 1; i <= 5; i++) {
            PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                    "400000000000000" + i, "Limit Tester", LocalDate.of(2030, 1, 1));
            ResponseEntity<PaymentCardResponseDto> response = restTemplate.postForEntity(
                    "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        PaymentCardRequestDto sixthCardDto = new PaymentCardRequestDto(
                "4000000000000006", "Limit Tester", LocalDate.of(2030, 1, 1));
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", sixthCardDto, Object.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void shouldCascadeDeleteCards_whenUserIsDeleted() {
        Long userId = createUser("Cascade", "Test", "cascade.test@example.com");
        PaymentCardRequestDto cardDto = new PaymentCardRequestDto(
                "4000000000000007", "Cascade Test", LocalDate.of(2030, 1, 1));
        ResponseEntity<PaymentCardResponseDto> createResponse = restTemplate.postForEntity(
                "/api/users/" + userId + "/cards", cardDto, PaymentCardResponseDto.class);
        Long cardId = createResponse.getBody().id();

        restTemplate.delete("/api/users/" + userId);

        ResponseEntity<Object> getCardResponse = restTemplate.getForEntity("/api/cards/" + cardId, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, getCardResponse.getStatusCode());
    }
}
