package com.innowise.academy.user_service.exception;

public class MaxActiveCardsExceededException extends RuntimeException {

    public MaxActiveCardsExceededException(Long userId, int maxActive) {
        super(ErrorCode.MAX_ACTIVE_CARDS_EXCEEDED.format(userId, maxActive));
    }
}
