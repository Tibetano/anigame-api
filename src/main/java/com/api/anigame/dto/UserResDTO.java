package com.api.anigame.dto;

import com.api.anigame.persistence.entity.enumerate.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record UserResDTO(
        UUID id,
        String username,
        String name,
        String lastName,
        String cpf,
        String email,
        Gender gender,
        LocalDate dateOfBirth
) {
}
