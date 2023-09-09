package com.userjwtsecurity.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.userjwtsecurity.demo.models.entities.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(String roleName);
}
