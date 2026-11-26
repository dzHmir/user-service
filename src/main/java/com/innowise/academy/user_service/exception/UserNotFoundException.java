package com.innowise.academy.user_service.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(ErrorCode.USER_NOT_FOUND.format(id));
    }
}
