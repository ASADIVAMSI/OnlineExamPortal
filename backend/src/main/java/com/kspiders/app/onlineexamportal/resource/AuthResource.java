package com.kspiders.app.onlineexamportal.resource;

import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing public endpoints for user registration (signup) and login authentication (signin).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    // ENDPOINT: POST /api/auth/signup creates a pending user account.
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request.fullName(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.from(user,
            "Registration successful. Wait for admin approval before starting an assessment."));
    }

    // ENDPOINT: POST /api/auth/signin verifies credentials and returns a token.
    @PostMapping("/signin")
    public AuthResponse signin(@Valid @RequestBody SigninRequest request) {
        AuthService.SigninResult result = authService.signin(request.email(), request.password());
        return AuthResponse.from(result.user(), result.token(), "Signin successful");
    }

    public record SignupRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must contain at least 6 characters") String password
    ) {
    }

    public record SigninRequest(
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Password is required") String password
    ) {
    }

    public record AuthResponse(Long id, String fullName, String email, String role,
                               String approvalStatus, String token, String message) {
        static AuthResponse from(User user, String message) {
            return from(user, null, message);
        }

        static AuthResponse from(User user, String token, String message) {
            return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), user.getApprovalStatus().name(), token, message);
        }
    }
}
