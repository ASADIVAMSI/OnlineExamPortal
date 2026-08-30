package com.kspiders.app.onlineexamportal.service;

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication service managing candidate registration, password verification, session token generation,
 * and active token lookup.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /** Concurrent map storing active authentication tokens mapped to logged-in User entities. */
    private final Map<String, User> activeSessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user account after validating email uniqueness.
     *
     * @param fullName Candidate's name.
     * @param email    Account email.
     * @param password Raw plaintext password to hash and store.
     * @return Saved User entity.
     */
    public User signup(String fullName, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email");
        }

        User user = new User(fullName.trim(), normalizedEmail, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Authenticates candidate credentials and issues an active session token.
     *
     * @param email    Account email.
     * @param password Password attempt.
     * @return SigninResult object containing the User entity and generated session token UUID.
     */
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

    /**
     * Resolves the User entity bound to an active session token.
     *
     * @param token Session token UUID string.
     * @return Bound User entity.
     * @throws ResponseStatusException HTTP 401 if token is missing or invalid.
     */
    public User userForToken(String token) {
        User user = activeSessions.get(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in is required");
        }
        return user;
    }

    /** Record wrapper for sign-in output. */
    public record SigninResult(User user, String token) {
    }
}
