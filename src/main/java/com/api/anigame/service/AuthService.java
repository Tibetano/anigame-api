package com.api.anigame.service;

import com.api.anigame.dto.UserCredentialsReqDTO;
import com.api.anigame.dto.UserCredentialsResDTO;
import com.api.anigame.persistence.entity.RoleEntity;
import com.api.anigame.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Value("${expire-times.access-token}")
    private Long accessTokenExpiresTime;
    @Value("${expire-times.refresh-token}")
    private Long refreshTokenExpiresTime;

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<?> login (UserCredentialsReqDTO userCredentialsReqDTO) {
        var user = userRepository.findByUsername(userCredentialsReqDTO.username());

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(userCredentialsReqDTO.password(),user.get().getPassword())){
            throw new BadCredentialsException("Invalid user or password!");
        }

        var now = Instant.now();
        var expiresIn = accessTokenExpiresTime;

        var scopes = user.get().getRoles().stream()
                .map(RoleEntity::getName).collect(Collectors.joining(" "));

        var accessTokenClaims = JwtClaimsSet.builder()
                .issuer("anigame-api")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("anigame-api")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiresTime))
                .claim("scope", scopes)
                .build();

        var accessTokenJwtValue = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        var refreshTokenJwtValue = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        return ResponseEntity.ok(new UserCredentialsResDTO(accessTokenJwtValue, refreshTokenJwtValue, expiresIn));
    }

}
