package com.innowise.academy.user_service.specification;

import com.innowise.academy.user_service.entity.PaymentCardEntity;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {
    public static Specification<PaymentCardEntity> hasUserName(String name) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("name"), name);
    }

    public static Specification<PaymentCardEntity> hasUserSurname(String surname) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("surname"), surname);
    }
}
