package com.innowise.academy.user_service.exception;

public class MaxActiveCardsExceededException extends RuntimeException {

    public MaxActiveCardsExceededException(Long userId) {
        super("User with id " + userId + " cannot have more than 5 active cards");
    }
}
