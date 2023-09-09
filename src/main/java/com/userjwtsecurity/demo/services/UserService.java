package com.userjwtsecurity.demo.services;

import java.util.List;
import java.util.Optional;

import com.userjwtsecurity.demo.models.dto.UserDto;

public interface UserService {

    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long id);

}
