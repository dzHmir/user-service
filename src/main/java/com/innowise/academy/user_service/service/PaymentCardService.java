package com.innowise.academy.user_service.service;

import com.innowise.academy.user_service.entity.PaymentCardEntity;
import com.innowise.academy.user_service.entity.UserEntity;
import com.innowise.academy.user_service.repository.PaymentCardRepository;
import com.innowise.academy.user_service.repository.UserRepository;
import com.innowise.academy.user_service.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public Page<PaymentCardEntity> findAll(String name, String surname, Pageable page) {
        Specification<PaymentCardEntity> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(PaymentCardSpecification.hasUserName(name));
        }

        if (surname != null && !surname.isBlank()) {
            spec = spec.and(PaymentCardSpecification.hasUserSurname(surname));
        }

        return paymentCardRepository.findAll(spec, page);
    }

    public PaymentCardEntity create(Long userId, PaymentCardEntity card) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        long cardCount = paymentCardRepository.countByUser_Id(userId);

        if (cardCount >= 5) {
            throw new IllegalStateException("User cannot have more than 5 cards");
        }

        card.setUser(userEntity);

        return paymentCardRepository.save(card);
    }

    public PaymentCardEntity findById(Long id) {
        return paymentCardRepository.findById(id).orElseThrow();
    }

    public List<PaymentCardEntity> findByUserId(Long userId) {
        return paymentCardRepository.findByUser_Id(userId);
    }

    public PaymentCardEntity update(Long id, PaymentCardEntity updatedCard) {
        PaymentCardEntity card = paymentCardRepository.findById(id).orElseThrow();
        card.setNumber(updatedCard.getNumber());
        card.setHolder(updatedCard.getHolder());
        card.setExpirationDate(updatedCard.getExpirationDate());
        return paymentCardRepository.save(card);
    }

    public PaymentCardEntity setActive(Long id, boolean active) {
        PaymentCardEntity card = paymentCardRepository.findById(id).orElseThrow();
        card.setActive(active);
        return paymentCardRepository.save(card);
    }
}