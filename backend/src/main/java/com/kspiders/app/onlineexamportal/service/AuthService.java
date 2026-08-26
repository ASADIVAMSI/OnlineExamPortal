package com.kspiders.app.onlineexamportal.service;

// Handles account creation, sign-in, and the token used by protected requests.

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> activeSessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(String fullName, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email");
        }

        User user = new User(fullName.trim(), normalizedEmail, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public SigninResult signin(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, user);
        return new SigninResult(user, token);
    }

    public User userForToken(String token) {
        User user = activeSessions.get(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in is required");
        }
        return user;
    }

    public record SigninResult(User user, String token) {
    }
}
