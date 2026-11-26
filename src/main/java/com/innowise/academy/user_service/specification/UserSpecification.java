package com.innowise.academy.user_service.specification;

import com.innowise.academy.user_service.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> hasName(String name) {
        return (root, query, cb) ->
                cb.equal(root.get("name"), name);
    }

    public static Specification<UserEntity> hasSurname(String surname) {
        return (root, query, cb) ->
                cb.equal(root.get("surname"), surname);
    }
}