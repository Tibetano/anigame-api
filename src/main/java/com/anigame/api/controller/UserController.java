package com.anigame.api.controller;

import com.anigame.api.dto.UserReqDTO;
import com.anigame.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/u")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser (@RequestBody UserReqDTO userReqDTO) {
        return ResponseEntity.ok(userService.create(userReqDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser (@PathVariable UUID id) {
        return userService.get(id);
    }

    @GetMapping
    public ResponseEntity<?> getAllUser () {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser (@PathVariable UUID id, @RequestBody UserReqDTO userReqDTO) {
        return userService.update(id, userReqDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser (@PathVariable UUID id) {
        return userService.delete(id);
    }

}
