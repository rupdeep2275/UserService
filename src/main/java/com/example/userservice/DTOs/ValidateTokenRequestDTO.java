package com.example.userservice.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequestDTO {
    private Long userId;
    private String token;
}
