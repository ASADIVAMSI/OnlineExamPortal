package com.kspiders.app.onlineexamportal.resource;

// Provides the signed-in user's approval and question-set assignment status.

import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.service.AuthService;
import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;

    public UserResource(AuthService authService, AssignmentRepository assignmentRepository) {
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
    }

    // ENDPOINT: GET /api/user/me returns approval and assignment status.
    @GetMapping("/me")
    public UserStatus currentUser(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.userForToken(token);
        String assignedSet = assignmentRepository.findByUserId(user.getId())
            .map(assignment -> assignment.getQuestionSet().getName()).orElse(null);
        return UserStatus.from(user, assignedSet);
    }

    public record UserStatus(Long id, String fullName, String email, String role,
                             String approvalStatus, String assignedSet) {
        static UserStatus from(User user, String assignedSet) {
            return new UserStatus(user.getId(), user.getFullName(), user.getEmail(),
            user.getRole().name(), user.getApprovalStatus().name(), assignedSet);
        }
    }
}
