package com.kspiders.app.onlineexamportal.resource;

// Exposes administrator actions for users, assignments, and submission reviews.

import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.entity.Assignment;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.service.AdminService;
import com.kspiders.app.onlineexamportal.dao.SubmissionRepository;
import com.kspiders.app.onlineexamportal.entity.Submission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminResource {

    private final AdminService adminService;
    private final QuestionSetRepository questionSetRepository;
    private final SubmissionRepository submissionRepository;

    public AdminResource(AdminService adminService, QuestionSetRepository questionSetRepository,
                         SubmissionRepository submissionRepository) {
        this.adminService = adminService;
        this.questionSetRepository = questionSetRepository;
        this.submissionRepository = submissionRepository;
    }

    // ENDPOINT: GET /api/admin/users lists users for administrator review.
    @GetMapping("/users")
    public List<UserSummary> users(@RequestHeader("X-Auth-Token") String token) {
        return adminService.users(token).stream().map(UserSummary::from).toList();
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
        return UserSummary.from(adminService.changeApproval(token, id, User.ApprovalStatus.APPROVED));
    }

    // ENDPOINT: PUT /api/admin/users/{id}/reject rejects a user.
    @PutMapping("/users/{id}/reject")
    public UserSummary reject(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        return UserSummary.from(adminService.changeApproval(token, id, User.ApprovalStatus.REJECTED));
    }

    // ENDPOINT: PUT /api/admin/users/{userId}/question-set/{questionSetId} assigns a set.
    @PutMapping("/users/{userId}/question-set/{questionSetId}")
    public AssignmentSummary assignQuestionSet(@RequestHeader("X-Auth-Token") String token,
                                                @PathVariable Long userId,
                                                @PathVariable Long questionSetId) {
        return AssignmentSummary.from(adminService.assignQuestionSet(token, userId, questionSetId));
    }

    public record UserSummary(Long id, String fullName, String email, String role, String approvalStatus) {
        static UserSummary from(User user) {
            return new UserSummary(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), user.getApprovalStatus().name());
        }
    }

    public record AssignmentSummary(Long id, Long userId, Long questionSetId, String questionSetName) {
        static AssignmentSummary from(Assignment assignment) {
            return new AssignmentSummary(assignment.getId(), assignment.getUser().getId(),
                assignment.getQuestionSet().getId(), assignment.getQuestionSet().getName());
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
