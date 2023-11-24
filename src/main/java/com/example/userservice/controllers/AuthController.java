package com.example.userservice.controllers;

import com.example.userservice.DTOs.*;
import com.example.userservice.exceptions.InvalidCredtentialsException;
import com.example.userservice.exceptions.UserAlreadyExistsException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.SessionStatus;
import com.example.userservice.models.User;
import com.example.userservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody User user) throws InvalidCredtentialsException {
        UserDTO userDTO;
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        try {
            LoginResponseDTO loginResponseDTO = authService.login(user.getEmail(), user.getPassword());
            userDTO = loginResponseDTO.getUserDTO();
            headers.add("AUTH_TOKEN", loginResponseDTO.getToken());
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userDTO, headers, HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDTO logoutRequestDTO){
        authService.logout(logoutRequestDTO.getToken(), logoutRequestDTO.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) throws UserAlreadyExistsException {
        UserDTO userDTO = authService.signUp(signUpRequestDTO.getEmail(), signUpRequestDTO.getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDTO validateTokenRequestDTO){
        SessionStatus sessionStatus = authService.validate(validateTokenRequestDTO.getToken(), validateTokenRequestDTO.getUserId());
        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }
}
