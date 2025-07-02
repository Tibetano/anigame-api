package com.api.anigame.dto;

public record UserCredentialsResDTO(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
