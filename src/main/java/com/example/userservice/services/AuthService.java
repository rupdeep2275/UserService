package com.example.userservice.services;

import com.example.userservice.DTOs.LoginResponseDTO;
import com.example.userservice.DTOs.UserDTO;
import com.example.userservice.exceptions.InvalidCredtentialsException;
import com.example.userservice.exceptions.UserAlreadyExistsException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.Session;
import com.example.userservice.models.SessionStatus;
import com.example.userservice.models.User;
import com.example.userservice.repositories.SessionRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder bCryptPasswordEncoder;

    public LoginResponseDTO login(String email, String password) throws UserNotFoundException, InvalidCredtentialsException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
            throw new UserNotFoundException("User with email " + email + " does not exist");
        }
        User user = userOptional.get();
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredtentialsException("Wrong password.");
        }

//        String token = RandomStringUtils.randomAlphanumeric(20);
        String jwt = jwtUtil.generateToken(user);
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("AUTH_TOKEN", jwt);

        Session session = Session.builder()
                .sessionStatus(SessionStatus.ACTIVE)
                .token(jwt)
                .user(user)
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .build();
        sessionRepository.save(session);
        return LoginResponseDTO.builder()
                .token(jwt)
                .userDTO(userRepository.save(user).toUserDTO())
                .build();
    }

    public void logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()){
            return;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.LOGGED_OUT);
        sessionRepository.save(session);
    }
    public UserDTO signUp(String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .roles(new HashSet<>())
                .build();
        return userRepository.save(user).toUserDTO();
    }

    public Optional<UserDTO> validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()){
            return Optional.empty();
        }
        Session session = sessionOptional.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return Optional.empty();
        }
        if (session.getExpiresAt().before(new Date())){
            session.setSessionStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            return Optional.empty();
        }
        User user = session.getUser();
        return Optional.of(user.toUserDTO());
    }
}
