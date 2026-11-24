package com.innowise.academy.user_service.exception;

public class PaymentCardNotFoundException extends RuntimeException {

    public PaymentCardNotFoundException(Long id) {
        super(ErrorCode.PAYMENT_CARD_NOT_FOUND.format(id));
    }
}
