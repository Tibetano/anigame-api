package com.anigame.api.controller;

import com.anigame.api.dto.RefreshTokenReqDTO;
import com.anigame.api.dto.UserCredentialsReqDTO;
import com.anigame.api.dto.UserReqDTO;
import com.anigame.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody UserCredentialsReqDTO credentialsReqDTO){
        return authService.login(credentialsReqDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshLogin (@RequestBody RefreshTokenReqDTO refreshTokenReqDTO){
        return authService.refreshLogin(refreshTokenReqDTO.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout (@RequestBody RefreshTokenReqDTO refreshTokenReqDTO) {
        return authService.revokeRefreshToken(refreshTokenReqDTO.refreshToken());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile (@RequestHeader("Authorization") String authorizationHeader) {
        return authService.getProfile(authorizationHeader.replace("Bearer ",""));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser (@RequestBody UserReqDTO userReqDTO, UriComponentsBuilder uriBuilder) {
        var newUser = authService.register(userReqDTO);
        URI location = uriBuilder.path("/users/{id}")
                .buildAndExpand(newUser.id())
                .toUri();
        return ResponseEntity.created(location).body(newUser);
    }
}
