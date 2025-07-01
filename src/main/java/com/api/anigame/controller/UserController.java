package com.api.anigame.controller;

import com.api.anigame.dto.UserReqDTO;
import com.api.anigame.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/u")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser (@RequestBody UserReqDTO userReqDTO) {
        return userService.create(userReqDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser (@PathVariable Long id) {
        return userService.get(id);
    }

    @GetMapping
    public ResponseEntity<?> getAllUser () {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser (@PathVariable Long id, @RequestBody UserReqDTO userReqDTO) {
        return userService.update(id, userReqDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser (@PathVariable Long id) {
        return userService.delete(id);
    }

}
