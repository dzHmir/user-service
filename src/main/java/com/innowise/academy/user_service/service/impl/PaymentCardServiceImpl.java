package com.innowise.academy.user_service.service.impl;

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
import com.innowise.academy.user_service.service.PaymentCardService;
import com.innowise.academy.user_service.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Value("${app.cards.max-active}")
    private int maxActiveCards;

    @Override
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

    @Override
    @Transactional
    public PaymentCardResponseDto create(Long userId, PaymentCardRequestDto dto) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (paymentCardRepository.countActiveCardsByUserId(userId) >= maxActiveCards) {
            throw new MaxActiveCardsExceededException(userId, maxActiveCards);
        }

        PaymentCardEntity cardEntity = paymentCardMapper.toEntity(dto);
        cardEntity.setUser(userEntity);
        PaymentCardEntity savedCard = paymentCardRepository.save(cardEntity);

        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentCardResponseDto findById(Long id) {
        PaymentCardEntity paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        return paymentCardMapper.toDto(paymentCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponseDto> findByUserId(Long userId) {
        List<PaymentCardEntity> paymentCards = paymentCardRepository.findByUser_Id(userId);

        return paymentCards.stream().map(paymentCardMapper::toDto).toList();
    }

    @Override
    @Transactional
    public PaymentCardResponseDto update(Long id, PaymentCardRequestDto dto) {
        PaymentCardEntity card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        paymentCardMapper.updateEntity(dto, card);
        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional
    public PaymentCardResponseDto setActive(Long id, boolean active) {
        PaymentCardEntity cardEntity = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        if (active && !cardEntity.isActive()) {
            long activeCount = paymentCardRepository.countActiveCardsByUserId(cardEntity.getUser().getId());
            if (activeCount >= maxActiveCards) {
                throw new MaxActiveCardsExceededException(cardEntity.getUser().getId(), maxActiveCards);
            }
        }

        paymentCardRepository.updateActiveStatus(id, active);
        cardEntity.setActive(active);

        return paymentCardMapper.toDto(cardEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PaymentCardEntity cardEntity = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        paymentCardRepository.delete(cardEntity);
    }
}