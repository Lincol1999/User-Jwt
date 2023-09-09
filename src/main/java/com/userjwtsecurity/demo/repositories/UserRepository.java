package com.userjwtsecurity.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.userjwtsecurity.demo.models.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}
