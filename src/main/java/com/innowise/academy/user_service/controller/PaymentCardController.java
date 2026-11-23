package com.innowise.academy.user_service.controller;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import com.innowise.academy.user_service.service.PaymentCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> findById(@PathVariable @Positive Long id) {
        PaymentCardResponseDto responseDto = paymentCardService.findById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequestDto dto) {
        PaymentCardResponseDto updatedCard = paymentCardService.update(id, dto);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentCardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentCardResponseDto> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        PaymentCardResponseDto updatedPayment = paymentCardService.setActive(id, active);
        return ResponseEntity.ok(updatedPayment);
    }
}
