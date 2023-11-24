package com.example.userservice.DTOs;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserDTO {
    private String email;
    private Set<Role> roles;
}
