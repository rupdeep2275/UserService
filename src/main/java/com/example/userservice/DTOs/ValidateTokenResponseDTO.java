package com.example.userservice.DTOs;

import com.example.userservice.models.SessionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidateTokenResponseDTO {
    private UserDTO userDTO;
    private SessionStatus sessionStatus;
}
