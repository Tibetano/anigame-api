package com.api.anigame.service;

import com.api.anigame.dto.UserCredentialsReqDTO;
import com.api.anigame.dto.UserCredentialsResDTO;
import com.api.anigame.persistence.entity.UserEntity;
import com.api.anigame.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${expire-times.access-token}")
    private Long accessTokenExpiresTime;
    @Value("${expire-times.refresh-token}")
    private Long refreshTokenExpiresTime;

    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
    }

    public UserCredentialsResDTO getCredentials (UserEntity user) {
        var now = Instant.now();
        var expiresIn = accessTokenExpiresTime;
        var accessToken = tokenService.createAccessToken(user, now, accessTokenExpiresTime);
        var refreshToken = tokenService.createRefreshToken(user, now, refreshTokenExpiresTime);
        return new UserCredentialsResDTO(accessToken, refreshToken, expiresIn);
    }

    public ResponseEntity<?> login (UserCredentialsReqDTO userCredentialsReqDTO) {
        var user = userRepository.findByUsername(userCredentialsReqDTO.username());
        if (user.isEmpty() || !bCryptPasswordEncoder.matches(userCredentialsReqDTO.password(),user.get().getPassword())) {
            throw new BadCredentialsException("Invalid user or password!");
        }
        return ResponseEntity.ok(getCredentials(user.get()));
    }

    @Transactional
    public ResponseEntity<?> refreshLogin (String oldRefreshToken) {
        var userId = tokenService.validateRefreshToken(oldRefreshToken)
                .getUser().getId().toString();
        var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found."));
        return ResponseEntity.ok(getCredentials(user));
    }

    public ResponseEntity<?> revokeRefreshToken (String refreshToken) {
        return ResponseEntity.ok(tokenService.revokeRefreshToken(refreshToken));
    }

    public ResponseEntity<?> getProfile(String token) {

        var userId = tokenService.getUserIdFromToken(token);

        return ResponseEntity.ok(userService.get(userId));
    }
}
