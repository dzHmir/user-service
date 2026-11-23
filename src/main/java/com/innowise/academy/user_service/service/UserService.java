package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.entity.PaymentCardEntity;
import com.innowise.academy.user_service.entity.UserEntity;
import com.innowise.academy.user_service.repository.UserRepository;
import com.innowise.academy.user_service.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserEntity> findAll(String name, String surname, Pageable page) {
        Specification<UserEntity> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(UserSpecification.hasName(name));
        }

        if (surname != null && !surname.isBlank()) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        return userRepository.findAll(spec, page);
    }

    public UserEntity create(UserEntity userEntity) {

        if (userEntity.getCards().size() > 5) {
            throw new IllegalStateException("User cannot have more than 5 cards");
        }

        for (PaymentCardEntity card : userEntity.getCards()) {
            card.setUser(userEntity);
        }

        return userRepository.save(userEntity);
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public UserEntity update(Long id, UserEntity updatedUserEntity) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        userEntity.setName(updatedUserEntity.getName());
        userEntity.setSurname(updatedUserEntity.getSurname());
        userEntity.setBirthDate(updatedUserEntity.getBirthDate());
        userEntity.setEmail(updatedUserEntity.getEmail());
        return userRepository.save(userEntity);
    }

    public UserEntity setActive(Long id, boolean active) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        userEntity.setActive(active);
        return userRepository.save(userEntity);
    }
}