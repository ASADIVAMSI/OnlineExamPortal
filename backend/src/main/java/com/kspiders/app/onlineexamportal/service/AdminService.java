package com.kspiders.app.onlineexamportal.service;

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.dao.NotificationRepository;
import com.kspiders.app.onlineexamportal.entity.Assignment;
import com.kspiders.app.onlineexamportal.entity.Notification;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Business service component for handling administrator capabilities, including candidate approvals,
 * test module assignments, and notification broadcasting.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;
    private final QuestionSetRepository questionSetRepository;
    private final NotificationRepository notificationRepository;

    public AdminService(UserRepository userRepository, AuthService authService,
                        AssignmentRepository assignmentRepository, QuestionSetRepository questionSetRepository,
                        NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
        this.questionSetRepository = questionSetRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<User> users(String token) {
        requireAdmin(token);
        return userRepository.findAll();
    }

    public User changeApproval(String token, Long userId, User.ApprovalStatus status) {
        requireAdmin(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setApprovalStatus(status);
        User saved = userRepository.save(user);

        Optional<Assignment> latestOpt = assignmentRepository.findTopByUserIdOrderByIdDesc(userId);
        if (status == User.ApprovalStatus.APPROVED) {
            Assignment assignment;
            if (latestOpt.isPresent() && latestOpt.get().getStatus() != Assignment.AssignmentStatus.COMPLETED) {
                assignment = latestOpt.get();
            } else {
                QuestionSet defaultSet = questionSetRepository.findAll().stream().findFirst().orElse(null);
                assignment = defaultSet != null ? new Assignment(saved, defaultSet) : null;
            }
            if (assignment != null) {
                assignment.setStatus(Assignment.AssignmentStatus.APPROVED);
                assignmentRepository.save(assignment);
            }
            notificationRepository.save(new Notification(saved, "Your registration has been approved and you can start your assignment."));
        } else if (status == User.ApprovalStatus.REJECTED) {
            if (latestOpt.isPresent() && latestOpt.get().getStatus() != Assignment.AssignmentStatus.COMPLETED) {
                Assignment a = latestOpt.get();
                a.setStatus(Assignment.AssignmentStatus.PENDING_APPROVAL);
                assignmentRepository.save(a);
            }
            notificationRepository.save(new Notification(saved, "Your registration was rejected by the administrator."));
        }

        return saved;
    }

    public Assignment assignQuestionSet(String token, Long userId, Long questionSetId) {
        requireAdmin(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        QuestionSet questionSet = questionSetRepository.findById(questionSetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question set not found"));
        Optional<Assignment> latestOpt = assignmentRepository.findTopByUserIdOrderByIdDesc(userId);
        Assignment assignment;
        if (latestOpt.isPresent() && latestOpt.get().getStatus() != Assignment.AssignmentStatus.COMPLETED) {
            assignment = latestOpt.get();
            assignment.setQuestionSet(questionSet);
        } else {
            assignment = new Assignment(user, questionSet);
        }
        assignment.setStatus(Assignment.AssignmentStatus.PENDING_APPROVAL);
        
        user.setApprovalStatus(User.ApprovalStatus.PENDING_APPROVAL);
        userRepository.save(user);

        return assignmentRepository.save(assignment);
    }

    private void requireAdmin(String token) {
        if (!authService.userForToken(token).getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access is required");
        }
    }
}
