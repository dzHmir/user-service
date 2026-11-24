package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDto> findAll(String name, String surname, Pageable page);

    UserResponseDto create(UserRequestDto dto);

    UserResponseDto findById(Long id);

    UserResponseDto update(Long id, UserRequestDto dto);

    UserResponseDto setActive(Long id, boolean active);

    void delete(Long id);
}
