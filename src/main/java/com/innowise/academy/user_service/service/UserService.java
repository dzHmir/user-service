package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.dto.request.UserRequestDto;
import com.innowise.academy.user_service.dto.response.UserResponseDto;
import com.innowise.academy.user_service.entity.UserEntity;
import com.innowise.academy.user_service.exception.UserNotFoundException;
import com.innowise.academy.user_service.mapper.UserMapper;
import com.innowise.academy.user_service.repository.UserRepository;
import com.innowise.academy.user_service.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> findAll(String name, String surname, Pageable page) {
        Specification<UserEntity> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(UserSpecification.hasName(name));
        }

        if (surname != null && !surname.isBlank()) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        return userRepository.findAll(spec, page).map(userMapper::toDto);
    }

    @Transactional
    public UserResponseDto create(UserRequestDto dto) {
        UserEntity userEntity = userMapper.toEntity(dto);
        UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto dto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(dto, userEntity);
        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserResponseDto setActive(Long id, boolean active) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userEntity.setActive(active);
        return userMapper.toDto(userEntity);
    }

    @Transactional
    public void delete(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(userEntity);
    }
}