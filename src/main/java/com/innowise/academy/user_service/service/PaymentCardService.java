package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.dto.request.PaymentCardRequestDto;
import com.innowise.academy.user_service.dto.response.PaymentCardResponseDto;
import com.innowise.academy.user_service.entity.PaymentCardEntity;
import com.innowise.academy.user_service.entity.UserEntity;
import com.innowise.academy.user_service.exception.MaxActiveCardsExceededException;
import com.innowise.academy.user_service.exception.PaymentCardNotFoundException;
import com.innowise.academy.user_service.exception.UserNotFoundException;
import com.innowise.academy.user_service.mapper.PaymentCardMapper;
import com.innowise.academy.user_service.repository.PaymentCardRepository;
import com.innowise.academy.user_service.repository.UserRepository;
import com.innowise.academy.user_service.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public Page<PaymentCardResponseDto> findAll(String name, String surname, Pageable page) {
        Specification<PaymentCardEntity> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(PaymentCardSpecification.hasUserName(name));
        }

        if (surname != null && !surname.isBlank()) {
            spec = spec.and(PaymentCardSpecification.hasUserSurname(surname));
        }

        return paymentCardRepository.findAll(spec, page).map(paymentCardMapper::toDto);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public PaymentCardResponseDto create(Long userId, PaymentCardRequestDto dto) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (paymentCardRepository.countActiveCardsByUserId(userId) >= 5) {
            throw new MaxActiveCardsExceededException(userId);
        }

        PaymentCardEntity cardEntity = paymentCardMapper.toEntity(dto);
        cardEntity.setUser(userEntity);
        PaymentCardEntity savedCard = paymentCardRepository.save(cardEntity);

        return paymentCardMapper.toDto(savedCard);
    }

    @Transactional(readOnly = true)
    public PaymentCardResponseDto findById(Long id) {
        PaymentCardEntity paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional(readOnly = true)
    public List<PaymentCardResponseDto> findByUserId(Long userId) {
        List<PaymentCardEntity> paymentCards = paymentCardRepository.findByUser_Id(userId);

        return paymentCards.stream().map(paymentCardMapper::toDto).toList();
    }

    @CacheEvict(value = "users", key = "#result.userId()")
    @Transactional
    public PaymentCardResponseDto update(Long id, PaymentCardRequestDto dto) {
        PaymentCardEntity card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        paymentCardMapper.updateEntity(dto, card);
        return paymentCardMapper.toDto(card);
    }

    @CacheEvict(value = "users", key = "#result.userId()")
    @Transactional
    public PaymentCardResponseDto setActive(Long id, boolean active) {
        PaymentCardEntity cardEntity = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        if (active && !cardEntity.isActive()) {
            long activeCount = paymentCardRepository.countActiveCardsByUserId(cardEntity.getUser().getId());
            if (activeCount >= 5) {
                throw new MaxActiveCardsExceededException(cardEntity.getUser().getId());
            }
        }

        paymentCardRepository.updateActiveStatus(id, active);
        cardEntity.setActive(active);

        return paymentCardMapper.toDto(cardEntity);
    }

    @Transactional
    public void delete(Long id) {
        PaymentCardEntity cardEntity = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        Long userId = cardEntity.getUser().getId();
        paymentCardRepository.delete(cardEntity);
        cacheManager.getCache("users").evict(userId);
    }
}