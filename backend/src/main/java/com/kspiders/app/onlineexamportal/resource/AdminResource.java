package com.kspiders.app.onlineexamportal.resource;

import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.entity.Assignment;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.service.AdminService;
import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.SubmissionRepository;
import com.kspiders.app.onlineexamportal.entity.Submission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing administrative API endpoints for user approval, assignment management,
 * question set listing, and exam submission review.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminResource {

    private final AdminService adminService;
    private final QuestionSetRepository questionSetRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public AdminResource(AdminService adminService, QuestionSetRepository questionSetRepository,
                         SubmissionRepository submissionRepository, AssignmentRepository assignmentRepository) {
        this.adminService = adminService;
        this.questionSetRepository = questionSetRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // ENDPOINT: GET /api/admin/users lists users for administrator review.
    @GetMapping("/users")
    public List<UserSummary> users(@RequestHeader("X-Auth-Token") String token) {
        return adminService.users(token).stream().map(user -> {
            Assignment assignment = assignmentRepository.findTopByUserIdOrderByIdDesc(user.getId()).orElse(null);
            return UserSummary.from(user, assignment);
        }).toList();
    }

    // ENDPOINT: GET /api/admin/question-sets lists assignable question sets.
    @GetMapping("/question-sets")
    public List<QuestionSetSummary> questionSets(@RequestHeader("X-Auth-Token") String token) {
        adminService.users(token);
        return questionSetRepository.findAll().stream().map(QuestionSetSummary::from).toList();
    }

    // ENDPOINT: GET /api/admin/submissions lists submitted assessments.
    @GetMapping("/submissions")
    public List<SubmissionSummary> submissions(@RequestHeader("X-Auth-Token") String token) {
        adminService.users(token);
        return submissionRepository.findAllByOrderBySubmittedAtDesc().stream().map(SubmissionSummary::from).toList();
    }

    // ENDPOINT: PUT /api/admin/users/{id}/approve approves a user.
    @PutMapping("/users/{id}/approve")
    public UserSummary approve(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        User user = adminService.changeApproval(token, id, User.ApprovalStatus.APPROVED);
        Assignment assignment = assignmentRepository.findTopByUserIdOrderByIdDesc(id).orElse(null);
        return UserSummary.from(user, assignment);
    }

    // ENDPOINT: PUT /api/admin/users/{id}/reject rejects a user.
    @PutMapping("/users/{id}/reject")
    public UserSummary reject(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        User user = adminService.changeApproval(token, id, User.ApprovalStatus.REJECTED);
        Assignment assignment = assignmentRepository.findTopByUserIdOrderByIdDesc(id).orElse(null);
        return UserSummary.from(user, assignment);
    }

    // ENDPOINT: PUT /api/admin/users/{userId}/question-set/{questionSetId} assigns a set.
    @PutMapping("/users/{userId}/question-set/{questionSetId}")
    public AssignmentSummary assignQuestionSet(@RequestHeader("X-Auth-Token") String token,
                                                @PathVariable Long userId,
                                                @PathVariable Long questionSetId) {
        return AssignmentSummary.from(adminService.assignQuestionSet(token, userId, questionSetId));
    }

    // ENDPOINT: PUT /api/admin/users/{userId}/assignment/approve approves the user assignment.
    @PutMapping("/users/{userId}/assignment/approve")
    public UserSummary approveAssignment(@RequestHeader("X-Auth-Token") String token,
                                          @PathVariable Long userId) {
        User user = adminService.changeApproval(token, userId, User.ApprovalStatus.APPROVED);
        Assignment assignment = assignmentRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        return UserSummary.from(user, assignment);
    }

    public record UserSummary(Long id, String fullName, String email, String role, String approvalStatus, String assignmentStatus) {
        static UserSummary from(User user) {
            return from(user, null);
        }
        static UserSummary from(User user, Assignment assignment) {
            String assignmentStatus = assignment != null ? assignment.getStatus().name() : null;
            String status = user.getApprovalStatus().name();
            if (assignment != null) {
                if (assignment.getStatus() == Assignment.AssignmentStatus.COMPLETED) {
                    status = "COMPLETED";
                } else if (assignment.getStatus() == Assignment.AssignmentStatus.APPROVED || user.getApprovalStatus() == User.ApprovalStatus.APPROVED) {
                    status = "APPROVED";
                } else if (assignment.getStatus() == Assignment.AssignmentStatus.PENDING_APPROVAL) {
                    status = "PENDING_APPROVAL";
                }
            } else if (user.getApprovalStatus() == User.ApprovalStatus.APPROVED) {
                status = "APPROVED";
            }
            return new UserSummary(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), status, assignmentStatus);
        }
    }

    public record AssignmentSummary(Long id, Long userId, Long questionSetId, String questionSetName, String status) {
        static AssignmentSummary from(Assignment assignment) {
            return new AssignmentSummary(assignment.getId(), assignment.getUser().getId(),
                assignment.getQuestionSet().getId(), assignment.getQuestionSet().getName(),
                assignment.getStatus().name());
        }
    }

    public record QuestionSetSummary(Long id, String name, String description) {
        static QuestionSetSummary from(QuestionSet questionSet) {
            return new QuestionSetSummary(questionSet.getId(), questionSet.getName(), questionSet.getDescription());
        }
    }

    public record SubmissionSummary(Long id, String userName, String email, String questionSetName,
                                    String submittedAt, String status, int totalMarks, int correctAnswers,
                                    int wrongAnswers, List<AnswerSummary> answers) {
        static SubmissionSummary from(Submission submission) {
            int correctAnswers = (int) submission.getAnswers().stream()
                .filter(answer -> answer.getSelectedOption().equals(answer.getQuestion().getCorrectOption()))
                .count();
            return new SubmissionSummary(submission.getId(), submission.getUser().getFullName(),
                submission.getUser().getEmail(), submission.getQuestionSet().getName(),
                submission.getSubmittedAt().toString(), submission.getStatus().name(),
                30, correctAnswers, 30 - correctAnswers,
                submission.getAnswers().stream().map(AnswerSummary::from).toList());
        }
    }

    public record AnswerSummary(Long questionId, String question, String selectedOption,
                                String correctOption, boolean correct) {
        static AnswerSummary from(com.kspiders.app.onlineexamportal.entity.UserAnswer answer) {
            String correctOption = answer.getQuestion().getCorrectOption();
            return new AnswerSummary(answer.getQuestion().getId(), answer.getQuestion().getQuestionText(),
                answer.getSelectedOption(), correctOption, answer.getSelectedOption().equals(correctOption));
        }
    }
}
