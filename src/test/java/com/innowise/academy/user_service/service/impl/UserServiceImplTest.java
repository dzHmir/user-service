package com.innowise.academy.user_service.service.impl;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import com.innowise.academy.user_service.dto.response.UserWithCardsResponseDto;
import com.innowise.academy.user_service.entity.UserEntity;
import com.innowise.academy.user_service.exception.UserNotFoundException;
import com.innowise.academy.user_service.mapper.UserMapper;
import com.innowise.academy.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = Pageable.unpaged();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Page<UserEntity> entityPage = new PageImpl<>(List.of(userEntity));

        UserResponseDto responseDto = new UserResponseDto(
                1L, "Ivan", "Petrov", LocalDate.of(1990, 5, 10), "ivan@example.com", true);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);
        when(userMapper.toDto(userEntity)).thenReturn(responseDto);

        Page<UserResponseDto> result = userService.findAll(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());
    }

    @Test
    void create_shouldMapAndSaveUser() {
        UserRequestDto requestDto = new UserRequestDto(
                "Dzmitry",
                "Hmir",
                LocalDate.of(1996, 2, 5),
                "dzmitry@example.com");

        UserEntity entityToSave = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);

        UserResponseDto expectedResponse = new UserResponseDto(
                1L,
                "Dzmitry",
                "Hmir",
                LocalDate.of(1996, 2, 5),
                "dzmitry@example.com",
                true);

        when(userMapper.toEntity(requestDto)).thenReturn(entityToSave);
        when(userRepository.save(entityToSave)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(expectedResponse);

        UserResponseDto result = userService.create(requestDto);

        assertEquals(expectedResponse, result);
        verify(userRepository).save(entityToSave);
    }

    @Test
    void update_shouldUpdateExistingUser() {
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto(
                "Ivan", "Updated", LocalDate.of(1990, 5, 10), "ivan@example.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        UserResponseDto expectedResponse = new UserResponseDto(
                userId, "Ivan", "Updated", LocalDate.of(1990, 5, 10), "ivan@example.com", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        UserResponseDto result = userService.update(userId, requestDto);

        assertEquals(expectedResponse, result);
        verify(userMapper).updateEntity(requestDto, userEntity);
    }

    @Test
    void update_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto(
                "Ivan", "Updated", LocalDate.of(1990, 5, 10), "ivan@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(userId, requestDto));
    }

    @Test
    void setActive_shouldUpdateActiveFlag() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActive(true);

        UserResponseDto expectedResponse = new UserResponseDto(
                userId, "Ivan", "Petrov", LocalDate.of(1990, 5, 10), "ivan@example.com", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        UserResponseDto result = userService.setActive(userId, false);

        assertEquals(expectedResponse, result);
        assertFalse(userEntity.isActive());
    }

    @Test
    void setActive_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.setActive(userId, true));
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        userService.delete(userId);

        verify(userRepository).delete(userEntity);
    }

    @Test
    void delete_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @Test
    void findByIdWithCards_shouldReturnUserWithCards() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        UserWithCardsResponseDto expectedResponse = new UserWithCardsResponseDto(
                userId, "Ivan", "Petrov", LocalDate.of(1990, 5, 10), "ivan@example.com", true, List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toWithCardsDto(userEntity)).thenReturn(expectedResponse);

        UserWithCardsResponseDto result = userService.findByIdWithCards(userId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void findByIdWithCards_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByIdWithCards(userId));
    }
}
