package com.userjwtsecurity.demo.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userjwtsecurity.demo.models.dto.UserDto;
import com.userjwtsecurity.demo.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "api/users/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> getUsers = userService.findAll();
            log.info("Users retrieved successfully");
            return new ResponseEntity<>(getUsers, HttpStatus.OK);

        } catch (RuntimeException e) {
            log.error("Error while getting all users");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto dto) {

        try {
            UserDto newUserDto = userService.save(dto);
            log.info("User created successfully");
            return new ResponseEntity<>(newUserDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while creating user");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto dto, @PathVariable Long id) {
        try {

            UserDto o = userService.update(dto, id);
            log.info("User updated successfully");
            return new ResponseEntity<>(o, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error while updating user", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<UserDto> o = userService.findById(id);
            if (o.isPresent()) {
                userService.delete(id);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
