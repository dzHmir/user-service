package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import com.innowise.academy.user_service.dto.response.UserWithCardsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDto> findAll(String name, String surname, Pageable page);

    UserResponseDto create(UserRequestDto dto);

    UserResponseDto update(Long id, UserRequestDto dto);

    UserResponseDto setActive(Long id, boolean active);

    void delete(Long id);

    UserWithCardsResponseDto findByIdWithCards(Long id);
}
