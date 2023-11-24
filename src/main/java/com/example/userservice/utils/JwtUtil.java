package com.example.userservice.utils;

import com.example.userservice.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();

        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles());

        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        String secret = "secretsecretsecretsecretsecretsecretsecretsecret";
        return Jwts.builder()
                .setClaims(claims)
                .setHeader(headers)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
