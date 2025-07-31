package com.anigame.api.dto;

import java.time.LocalDate;

public record UserReqDTO(
        String username,
        String password,
        String firstName,
        String lastName,
        String cpf,
        String email,
        String gender,
        LocalDate dateOfBirth
) {
}
