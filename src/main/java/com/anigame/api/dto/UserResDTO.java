package com.anigame.api.dto;

import com.anigame.api.persistence.entity.enumerate.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record UserResDTO(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String cpf,
        String email,
        Gender gender,
        LocalDate dateOfBirth
) {
}
