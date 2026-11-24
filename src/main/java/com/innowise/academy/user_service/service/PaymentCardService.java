package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

    Page<PaymentCardResponseDto> findAll(String name, String surname, Pageable page);

    PaymentCardResponseDto create(Long userId, PaymentCardRequestDto dto);

    PaymentCardResponseDto findById(Long id);

    List<PaymentCardResponseDto> findByUserId(Long userId);

    PaymentCardResponseDto update(Long id, PaymentCardRequestDto dto);

    PaymentCardResponseDto setActive(Long id, boolean active);

    void delete(Long id);
}
