package com.example.userservice.models;

import com.example.userservice.DTOs.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public UserDTO toUserDTO() {
        return UserDTO.builder()
                .email(this.email)
                .roles(this.roles)
                .build();
    }
}
