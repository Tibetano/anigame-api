package com.anigame.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenReqDTO(
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
