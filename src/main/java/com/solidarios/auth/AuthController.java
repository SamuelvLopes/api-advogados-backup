package com.solidarios.auth;

import com.solidarios.security.JwtService;
import com.solidarios.user.User;
import com.solidarios.user.UserRepository;
import com.solidarios.user.UserRole;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        if (request.getRole() == UserRole.LAWYER) {
            user.setOab(request.getOab());
            user.setAreas(request.getAreas());
            user.setWhatsapp(request.getWhatsapp());
        }
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(request.getEmail());
        return new TokenResponse(token);
    }

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private UserRole role;
        private String oab;
        private String areas;
        private String whatsapp;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class TokenResponse {
        private final String token;
    }
}
