package com.kspiders.app.onlineexamportal.resource;

// Serves assigned questions and accepts the user's completed assessment.

import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionRepository;
import com.kspiders.app.onlineexamportal.entity.Question;
import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.service.AuthService;
import com.kspiders.app.onlineexamportal.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentResource {

    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final SubmissionService submissionService;

    public AssessmentResource(AuthService authService, AssignmentRepository assignmentRepository,
                              QuestionRepository questionRepository, SubmissionService submissionService) {
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
        this.questionRepository = questionRepository;
        this.submissionService = submissionService;
    }

    // ENDPOINT: GET /api/assessment/questions returns the approved user's assigned questions.
    @GetMapping("/questions")
    public AssessmentResponse questions(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.userForToken(token);
        if (user.getApprovalStatus() != User.ApprovalStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin approval is required");
        }
        var assignment = assignmentRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "A question set has not been assigned"));
        List<QuestionView> questions = questionRepository.findByQuestionSetIdOrderById(assignment.getQuestionSet().getId())
            .stream().map(QuestionView::from).toList();
        return new AssessmentResponse(assignment.getQuestionSet().getName(), questions);
    }

    // ENDPOINT: POST /api/assessment/submit saves answers and calculates the result.
    @PostMapping("/submit")
    public SubmissionResponse submit(@RequestHeader("X-Auth-Token") String token,
                                     @org.springframework.web.bind.annotation.RequestBody SubmitRequest request) {
        var submission = submissionService.submit(token, request.answers());
        return new SubmissionResponse(submission.getId(), submission.getStatus().name(), submission.getSubmittedAt().toString(),
            submission.getTotalMarks(), submission.getCorrectAnswers(), submission.getWrongAnswers());
    }

    public record AssessmentResponse(String questionSetName, List<QuestionView> questions) {}

    public record SubmitRequest(List<SubmissionService.AnswerInput> answers) {}

    public record SubmissionResponse(Long id, String status, String submittedAt, int totalMarks,
                                     int correctAnswers, int wrongAnswers) {}

    public record QuestionView(Long id, String questionText, String optionA, String optionB,
                               String optionC, String optionD, String questionType) {
        static QuestionView from(Question question) {
            return new QuestionView(question.getId(), question.getQuestionText(), question.getOptionA(),
                question.getOptionB(), question.getOptionC(), question.getOptionD(), question.getQuestionType());
        }
    }
}
