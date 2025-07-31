package com.anigame.api.service;

import com.anigame.api.dto.UserReqDTO;
import com.anigame.api.dto.UserResDTO;
import com.anigame.api.persistence.entity.UserEntity;
import com.anigame.api.persistence.entity.enumerate.Gender;
import com.anigame.api.persistence.entity.RoleEntity;
import com.anigame.api.persistence.repository.RoleRepository;
import com.anigame.api.persistence.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserResDTO create (UserReqDTO userReqDTO) {
        var userRole = roleRepository.findByName(RoleEntity.Values.BASIC.name());
        var encodedPassword = bCryptPasswordEncoder.encode(userReqDTO.password());

        if (userRole.isEmpty()) {
            throw new RuntimeException("Role not found.");
        }

        var newUser = UserEntity.builder()
                .username(userReqDTO.username())
                .password(encodedPassword)
                .firstName(userReqDTO.firstName())
                .lastName(userReqDTO.lastName())
                .cpf(userReqDTO.cpf())
                .email(userReqDTO.email())
                .gender(Gender.valueOf(userReqDTO.gender()))
                .dateOfBirth(userReqDTO.dateOfBirth())
                .roles(Set.of(userRole.get()))
                .build();
        var savedUser = userRepository.save(newUser);

        return new UserResDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getCpf(),
                savedUser.getEmail(),
                savedUser.getGender(),
                savedUser.getDateOfBirth()
        );
    }

    public ResponseEntity<?> get (UUID id) {
        var user = userRepository.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    public ResponseEntity<?> getAll () {
        var userList = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    public ResponseEntity<?> update (UUID id, UserReqDTO userReqDTO) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        user.get().setUsername(userReqDTO.username() != null ? userReqDTO.username() : user.get().getUsername());
        user.get().setPassword(userReqDTO.password() != null ? bCryptPasswordEncoder.encode(userReqDTO.password()) : user.get().getPassword());
        userRepository.save(user.get());
        return ResponseEntity.status(HttpStatus.OK).body("User updated.");
    }

    public ResponseEntity<?> delete (UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted.");
    }

    public UserEntity findByUsername (String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new BadCredentialsException("Invalid user or password!");
        }
        return user.get();
    }

    public UserEntity findById (UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return user;
    }

}
