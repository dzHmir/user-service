package com.innowise.academy.user_service.controller;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import com.innowise.academy.user_service.service.PaymentCardService;
import com.innowise.academy.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable @Positive Long id) {
        UserResponseDto userResponseDto = userService.findById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable) {
        Page<UserResponseDto> users = userService.findAll(name, surname, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(
            @Valid @RequestBody UserRequestDto dto) {

        UserResponseDto createdUser = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {
        UserResponseDto updatedUser = userService.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        UserResponseDto updatedUser = userService.setActive(id, active);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/cards")
    public ResponseEntity<PaymentCardResponseDto> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentCardRequestDto dto) {
        PaymentCardResponseDto createdCard = paymentCardService.create(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponseDto>> findCardsByUserId(@PathVariable Long userId) {
        List<PaymentCardResponseDto> cards = paymentCardService.findByUserId(userId);
        return ResponseEntity.ok(cards);
    }
}
