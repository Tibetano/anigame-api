package com.anigame.api.service;

import com.anigame.api.persistence.entity.RefreshTokenEntity;
import com.anigame.api.persistence.entity.UserEntity;
import com.anigame.api.persistence.entity.RoleEntity;
import com.anigame.api.persistence.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, RefreshTokenRepository refreshTokenRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Transactional
    public RefreshTokenEntity validateRefreshToken(String token) {
        var decodedJwt = jwtDecoder.decode(token);
        var dbRefreshToken = refreshTokenRepository.findById(UUID.fromString(decodedJwt.getId()));
        if (dbRefreshToken.isEmpty()) {
            throw new RuntimeException("Refresh Token not found.");
        } else {
            refreshTokenRepository.deleteById(dbRefreshToken.get().getId());
        }
        Instant truncatedD1 = decodedJwt.getExpiresAt().truncatedTo(ChronoUnit.SECONDS);
        Instant truncatedD2 = dbRefreshToken.get().getExpiresAt().truncatedTo(ChronoUnit.SECONDS);
        if (!truncatedD1.equals(truncatedD2)){
            throw new RuntimeException("Invalid refresh token data. Erro: different dates.");
        }
        if (!Objects.equals(decodedJwt.getId(), dbRefreshToken.get().getId().toString())) {
            throw new RuntimeException("Invalid Refresh Token.");
        }
        return dbRefreshToken.get();
    }

    public String createAccessToken (UserEntity user, Instant instant, Long expiresIn) {
        var scopes = user.getRoles().stream()
                .map(RoleEntity::getName).collect(Collectors.joining(" "));
        var accessTokenClaims = JwtClaimsSet.builder()
                .issuer("anigame-api")
                .subject(user.getId().toString())
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
    }

    public String createRefreshToken (UserEntity user, Instant instant, Long expiresIn) {
        var refreshToken = refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .expiresAt(instant.plusSeconds(expiresIn))
                        .user(user)
                        .build()
        );
        var accessTokenClaims = JwtClaimsSet.builder()
                .issuer("anigame-api")
                .subject(user.getId().toString())
                .id(refreshToken.getId().toString())
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(expiresIn))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
    }

    public String revokeRefreshToken(String refreshToken){
        System.out.println("Chegei aqui pae!!!!!!!!!!!");
        var validatedToken = validateRefreshToken(refreshToken);
        refreshTokenRepository.deleteById(validatedToken.getId());
        return "RefreshToken revoked successfully!";
    }

    public UUID getUserIdFromToken (String token) {
        return UUID.fromString(jwtDecoder.decode(token).getSubject());
    }
}
