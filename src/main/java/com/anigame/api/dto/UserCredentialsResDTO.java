package com.anigame.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCredentialsResDTO(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("expires_in")
        Long expiresIn
) {
}
