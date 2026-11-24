package com.innowise.academy.user_service.exception;

public enum ErrorCode {
    USER_NOT_FOUND("User not found with id: %d"),
    PAYMENT_CARD_NOT_FOUND("Payment card not found with id: %d"),
    MAX_ACTIVE_CARDS_EXCEEDED("User with id %d cannot have more than %d active cards");

    private final String template;

    ErrorCode(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }
}