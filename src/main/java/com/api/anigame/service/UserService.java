package com.api.anigame.service;

import com.api.anigame.dto.UserReqDTO;
import com.api.anigame.persistence.entity.UserEntity;
import com.api.anigame.persistence.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> create (UserReqDTO userReqDTO) {

        var newUser = UserEntity.builder()
                .username(userReqDTO.username())
                .password(userReqDTO.password())
                .build();

        var savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    public ResponseEntity<?> get (Long id) {

        var user = userRepository.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    public ResponseEntity<?> getAll () {

        var userList = userRepository.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    public ResponseEntity<?> update (Long id, UserReqDTO userReqDTO) {

        var user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        user.get().setUsername(userReqDTO.username() != null ? userReqDTO.username() : user.get().getUsername());
        user.get().setPassword(userReqDTO.password() != null ? userReqDTO.password() : user.get().getPassword());

        userRepository.save(user.get());

        return ResponseEntity.status(HttpStatus.OK).body("User updated.");
    }

    public ResponseEntity<?> delete (Long id) {

        userRepository.deleteById(id);

        return ResponseEntity.ok("User deleted.");
    }

}
