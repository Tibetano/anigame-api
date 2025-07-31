package com.anigame.api.service;

import com.anigame.api.dto.UserResDTO;
import com.anigame.api.persistence.entity.UserEntity;
import com.anigame.api.dto.UserCredentialsReqDTO;
import com.anigame.api.dto.UserCredentialsResDTO;
import com.anigame.api.dto.UserReqDTO;
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

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    public AuthService(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, TokenService tokenService) {
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
        var user = userService.findByUsername(userCredentialsReqDTO.username());
        if (!bCryptPasswordEncoder.matches(userCredentialsReqDTO.password(),user.getPassword())) {
            throw new BadCredentialsException("Invalid user or password!");
        }
        return ResponseEntity.ok(getCredentials(user));
    }

    @Transactional
    public ResponseEntity<?> refreshLogin (String oldRefreshToken) {
        var userId = tokenService.validateRefreshToken(oldRefreshToken)
                .getUser().getId().toString();
        var user = userService.findById(UUID.fromString(userId));
        return ResponseEntity.ok(getCredentials(user));
    }

    public ResponseEntity<?> revokeRefreshToken (String refreshToken) {
        return ResponseEntity.ok(tokenService.revokeRefreshToken(refreshToken));
    }

    public ResponseEntity<?> getProfile(String token) {
        var userId = tokenService.getUserIdFromToken(token);
        return userService.get(userId);
    }

    public UserResDTO register (UserReqDTO userReqDTO) {
        return userService.create(userReqDTO);
    }
}
