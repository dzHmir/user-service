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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    private static final int MAX_ACTIVE_CARDS = 5;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentCardService, "maxActiveCards", MAX_ACTIVE_CARDS);
    }

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = Pageable.unpaged();

        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(1L);

        Page<PaymentCardEntity> entityPage = new PageImpl<>(List.of(cardEntity));

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto(
                1L, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), true, 1L);

        when(paymentCardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(responseDto);

        Page<PaymentCardResponseDto> result = paymentCardService.findAll(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().getFirst());
    }

    @Test
    void create_shouldCreateCard_whenUserExistsAndUnderLimit() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentCardRequestDto requestDto = new PaymentCardRequestDto(
                "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1));

        PaymentCardEntity entityToSave = new PaymentCardEntity();
        PaymentCardEntity savedEntity = new PaymentCardEntity();
        savedEntity.setId(1L);

        PaymentCardResponseDto expectedResponse = new PaymentCardResponseDto(
                1L, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), true, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(paymentCardRepository.countActiveCardsByUserId(userId)).thenReturn(0L);
        when(paymentCardMapper.toEntity(requestDto)).thenReturn(entityToSave);
        when(paymentCardRepository.save(entityToSave)).thenReturn(savedEntity);
        when(paymentCardMapper.toDto(savedEntity)).thenReturn(expectedResponse);

        PaymentCardResponseDto result = paymentCardService.create(userId, requestDto);

        assertEquals(expectedResponse, result);
        assertEquals(userEntity, entityToSave.getUser());
    }

    @Test
    void create_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto(
                "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> paymentCardService.create(userId, requestDto));
    }

    @Test
    void create_shouldThrowMaxActiveCardsExceededException_whenLimitReached() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentCardRequestDto requestDto = new PaymentCardRequestDto(
                "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(paymentCardRepository.countActiveCardsByUserId(userId)).thenReturn((long) MAX_ACTIVE_CARDS);

        assertThrows(MaxActiveCardsExceededException.class,
                () -> paymentCardService.create(userId, requestDto));
        verify(paymentCardRepository, never()).save(any(PaymentCardEntity.class));
    }

    @Test
    void findById_shouldReturnCard_whenCardExists() {
        Long cardId = 1L;
        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);

        PaymentCardResponseDto expectedResponse = new PaymentCardResponseDto(
                cardId, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), true, 1L);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(expectedResponse);

        PaymentCardResponseDto result = paymentCardService.findById(cardId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void findById_shouldThrowPaymentCardNotFoundException_whenCardDoesNotExist() {
        Long cardId = 1L;
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.findById(cardId));
    }

    @Test
    void findByUserId_shouldReturnAllCardsForUser() {
        Long userId = 1L;
        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(1L);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto(
                1L, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), true, userId);

        when(paymentCardRepository.findByUser_Id(userId)).thenReturn(List.of(cardEntity));
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(responseDto);

        List<PaymentCardResponseDto> result = paymentCardService.findByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.getFirst());
    }

    @Test
    void update_shouldUpdateExistingCard() {
        Long cardId = 1L;
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto(
                "4000000000009999", "Updated Holder", LocalDate.of(2031, 1, 1));

        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);

        PaymentCardResponseDto expectedResponse = new PaymentCardResponseDto(
                cardId, "4000000000009999", "Updated Holder", LocalDate.of(2031, 1, 1), true, 1L);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(expectedResponse);

        PaymentCardResponseDto result = paymentCardService.update(cardId, requestDto);

        assertEquals(expectedResponse, result);
        verify(paymentCardMapper).updateEntity(requestDto, cardEntity);
    }

    @Test
    void update_shouldThrowPaymentCardNotFoundException_whenCardDoesNotExist() {
        Long cardId = 1L;
        PaymentCardRequestDto requestDto = new PaymentCardRequestDto(
                "4000000000009999", "Updated Holder", LocalDate.of(2031, 1, 1));

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.update(cardId, requestDto));
    }

    @Test
    void setActive_shouldDeactivateCard_withoutLimitCheck() {
        Long cardId = 1L;
        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);
        cardEntity.setActive(true);

        PaymentCardResponseDto expectedResponse = new PaymentCardResponseDto(
                cardId, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), false, 1L);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(expectedResponse);

        PaymentCardResponseDto result = paymentCardService.setActive(cardId, false);

        assertEquals(expectedResponse, result);
        verify(paymentCardRepository, never()).countActiveCardsByUserId(any(Long.class));
        verify(paymentCardRepository).updateActiveStatus(cardId, false);
    }

    @Test
    void setActive_shouldReactivateCard_whenUnderLimit() {
        Long cardId = 1L;
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);
        cardEntity.setActive(false);
        cardEntity.setUser(userEntity);

        PaymentCardResponseDto expectedResponse = new PaymentCardResponseDto(
                cardId, "4000000000000001", "Ivan Petrov", LocalDate.of(2030, 1, 1), true, userId);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(paymentCardRepository.countActiveCardsByUserId(userId)).thenReturn(3L);
        when(paymentCardMapper.toDto(cardEntity)).thenReturn(expectedResponse);

        PaymentCardResponseDto result = paymentCardService.setActive(cardId, true);

        assertEquals(expectedResponse, result);
        verify(paymentCardRepository).updateActiveStatus(cardId, true);
    }

    @Test
    void setActive_shouldThrowMaxActiveCardsExceededException_whenReactivatingAtLimit() {
        Long cardId = 1L;
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);
        cardEntity.setActive(false);
        cardEntity.setUser(userEntity);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(paymentCardRepository.countActiveCardsByUserId(userId)).thenReturn((long) MAX_ACTIVE_CARDS);

        assertThrows(MaxActiveCardsExceededException.class,
                () -> paymentCardService.setActive(cardId, true));
        verify(paymentCardRepository, never()).updateActiveStatus(any(Long.class), anyBoolean());
    }

    @Test
    void setActive_shouldThrowPaymentCardNotFoundException_whenCardDoesNotExist() {
        Long cardId = 1L;
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.setActive(cardId, true));
    }

    @Test
    void delete_shouldDeleteCardAndEvictUserCache() {
        Long cardId = 1L;
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentCardEntity cardEntity = new PaymentCardEntity();
        cardEntity.setId(cardId);
        cardEntity.setUser(userEntity);

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cacheManager.getCache("users")).thenReturn(cache);

        paymentCardService.delete(cardId);

        verify(paymentCardRepository).delete(cardEntity);
        verify(cache).evict(userId);
    }

    @Test
    void delete_shouldThrowPaymentCardNotFoundException_whenCardDoesNotExist() {
        Long cardId = 1L;
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.delete(cardId));
        verify(paymentCardRepository, never()).delete(any(PaymentCardEntity.class));
    }
}
