package com.kspiders.app.onlineexamportal.resource;

import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.entity.Notification;
import com.kspiders.app.onlineexamportal.entity.Assignment;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import com.kspiders.app.onlineexamportal.service.AuthService;
import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.NotificationRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.dao.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST Controller providing candidate dashboard endpoints for retrieving account status,
 * requesting new assessment assignments, and reading notifications.
 */
@RestController
@RequestMapping("/api/user")
public class UserResource {

    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;
    private final NotificationRepository notificationRepository;
    private final QuestionSetRepository questionSetRepository;
    private final UserRepository userRepository;

    public UserResource(AuthService authService,
                        AssignmentRepository assignmentRepository,
                        NotificationRepository notificationRepository,
                        QuestionSetRepository questionSetRepository,
                        UserRepository userRepository) {
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
        this.notificationRepository = notificationRepository;
        this.questionSetRepository = questionSetRepository;
        this.userRepository = userRepository;
    }

    // ENDPOINT: GET /api/user/me returns approval and assignment status.
    @GetMapping("/me")
    public UserStatus currentUser(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.userForToken(token);
        var assignmentOpt = assignmentRepository.findTopByUserIdOrderByIdDesc(user.getId());
        String assignedSet = assignmentOpt.map(a -> a.getQuestionSet().getName()).orElse(null);

        String status = user.getApprovalStatus().name();
        if (assignmentOpt.isPresent()) {
            Assignment.AssignmentStatus asmStatus = assignmentOpt.get().getStatus();
            if (asmStatus == Assignment.AssignmentStatus.COMPLETED) {
                status = "COMPLETED";
            } else if (asmStatus == Assignment.AssignmentStatus.APPROVED || user.getApprovalStatus() == User.ApprovalStatus.APPROVED) {
                status = "APPROVED";
            } else if (asmStatus == Assignment.AssignmentStatus.PENDING_APPROVAL) {
                status = "PENDING_APPROVAL";
            }
        } else if (user.getApprovalStatus() == User.ApprovalStatus.APPROVED) {
            status = "APPROVED";
        }

        return UserStatus.from(user, status, assignedSet);
    }

    @PostMapping("/request-assessment")
    public UserStatus requestNewAssessment(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.userForToken(token);
        var latestOpt = assignmentRepository.findTopByUserIdOrderByIdDesc(user.getId());

        if (latestOpt.isPresent() && latestOpt.get().getStatus() != Assignment.AssignmentStatus.COMPLETED) {
            return currentUser(token);
        }

        QuestionSet defaultSet = questionSetRepository.findAll().stream().findFirst().orElse(null);
        if (defaultSet != null) {
            Assignment newAssignment = new Assignment(user, defaultSet);
            newAssignment.setStatus(Assignment.AssignmentStatus.PENDING_APPROVAL);
            assignmentRepository.save(newAssignment);
        }

        user.setApprovalStatus(User.ApprovalStatus.PENDING_APPROVAL);
        userRepository.save(user);

        notificationRepository.save(new Notification(user, "Your request for a new assessment has been submitted and is pending admin approval."));
        return currentUser(token);
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.userForToken(token);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public record UserStatus(Long id, String fullName, String email, String role,
                             String approvalStatus, String assignedSet) {
        static UserStatus from(User user, String status, String assignedSet) {
            return new UserStatus(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), status, assignedSet);
        }
    }
}
