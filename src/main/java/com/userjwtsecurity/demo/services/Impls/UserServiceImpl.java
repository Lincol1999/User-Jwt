package com.userjwtsecurity.demo.services.Impls;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userjwtsecurity.demo.models.constants.ERole;
import com.userjwtsecurity.demo.models.dto.UserDto;
import com.userjwtsecurity.demo.models.entities.RoleEntity;
import com.userjwtsecurity.demo.models.entities.UserEntity;
import com.userjwtsecurity.demo.repositories.RoleRepository;
import com.userjwtsecurity.demo.repositories.UserRepository;
import com.userjwtsecurity.demo.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    // private BCryptPasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        return users.stream().map(u -> modelMapper.map(u, UserDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {

        return userRepository.findById(id)
                .map(u -> modelMapper.map(u, UserDto.class));
        // return Optional.empty();
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {

        String passwordBC = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(passwordBC);

        userDto.setRoles(getRoles(userDto));

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        return modelMapper.map(userRepository.save(userEntity), UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {

        Optional<UserEntity> optionalUser = userRepository.findById(id);
        UserEntity users = null;

        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.orElseThrow(() -> new RuntimeException("User not found!"));
            String passwordBC = passwordEncoder.encode(userDto.getPassword());

            userEntity.setUsername(userDto.getUsername());
            userEntity.setPassword(passwordBC);
            userEntity.setEmail(userDto.getEmail());
            userEntity.setFirstname(userDto.getFirstname());
            userEntity.setLastname(userDto.getLastname());
            userEntity.setRoles(getRoles(userDto));

            users = userRepository.save(userEntity);
        }

        return modelMapper.map(users, UserDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private Set<RoleEntity> getRoles(UserDto user) {

        Set<RoleEntity> strGetRoles = user.getRoles();

        Set<String> strRoles = strGetRoles.stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toSet());

        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles == null) {

            RoleEntity userRole = roleRepository.findByRoleName(ERole.ROLE_USER.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
            roles.add(userRole);

        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "Admin":
                        // case "ROLE_ADMIN":
                        // case "ROLE_SUPERVISOR":
                        RoleEntity adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(adminRole);
                        break;

                    case "Supervisor":
                        RoleEntity supervisorRole = roleRepository.findByRoleName(ERole.ROLE_SUPERVISOR.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(supervisorRole);
                        break;

                    default:
                        RoleEntity userRole = roleRepository.findByRoleName(ERole.ROLE_USER.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(userRole);
                        break;
                }
            });
        }

        return roles;

    }

}
