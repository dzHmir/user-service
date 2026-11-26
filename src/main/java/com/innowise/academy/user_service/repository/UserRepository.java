package com.innowise.academy.user_service.repository;

import com.innowise.academy.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository
        extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {
}
