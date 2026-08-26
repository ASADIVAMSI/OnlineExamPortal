package com.kspiders.app.onlineexamportal.service;

// Contains administrator operations such as approval and question-set assignment.

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.entity.Assignment;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;
    private final QuestionSetRepository questionSetRepository;

    public AdminService(UserRepository userRepository, AuthService authService,
                        AssignmentRepository assignmentRepository, QuestionSetRepository questionSetRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
        this.questionSetRepository = questionSetRepository;
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
        return userRepository.save(user);
    }

    public Assignment assignQuestionSet(String token, Long userId, Long questionSetId) {
        requireAdmin(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        QuestionSet questionSet = questionSetRepository.findById(questionSetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question set not found"));
        Assignment assignment = assignmentRepository.findByUserId(userId)
            .orElseGet(() -> new Assignment(user, questionSet));
        assignment.setQuestionSet(questionSet);
        return assignmentRepository.save(assignment);
    }

    private void requireAdmin(String token) {
        if (!authService.userForToken(token).getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access is required");
        }
    }
}
