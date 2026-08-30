package com.kspiders.app.onlineexamportal.service;

import com.kspiders.app.onlineexamportal.dao.AssignmentRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionRepository;
import com.kspiders.app.onlineexamportal.dao.SubmissionRepository;
import com.kspiders.app.onlineexamportal.entity.Question;
import com.kspiders.app.onlineexamportal.entity.Submission;
import com.kspiders.app.onlineexamportal.entity.User;
import com.kspiders.app.onlineexamportal.entity.UserAnswer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service component responsible for processing candidate exam submissions, verifying 30-question completion rules,
 * computing final scores, and updating assignment status to COMPLETED.
 */
@Service
public class SubmissionService {

    private final AuthService authService;
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;

    public SubmissionService(AuthService authService, AssignmentRepository assignmentRepository,
                             QuestionRepository questionRepository, SubmissionRepository submissionRepository) {
        this.authService = authService;
        this.assignmentRepository = assignmentRepository;
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public Submission submit(String token, List<AnswerInput> inputs) {
        User user = authService.userForToken(token);
        if (user.getApprovalStatus() != User.ApprovalStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin approval is required");
        }
        var assignment = assignmentRepository.findTopByUserIdOrderByIdDesc(user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "A question set has not been assigned"));
        if (assignment.getStatus() == com.kspiders.app.onlineexamportal.entity.Assignment.AssignmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This assessment assignment has already been completed");
        }
        List<Question> questions = questionRepository.findByQuestionSetIdOrderById(assignment.getQuestionSet().getId());
        if (questions.size() != 30 || inputs == null || inputs.size() != 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exactly 30 answers are required");
        }
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, Function.identity()));
        Submission submission = new Submission(user, assignment.getQuestionSet());
        int correctAnswers = 0;
        for (AnswerInput input : inputs) {
            Question question = questionMap.get(input.questionId());
            if (question == null || input.selectedOption() == null || !input.selectedOption().matches("[ABCD]")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every answer must belong to the assigned set and use A, B, C, or D");
            }
            if (input.selectedOption().equals(question.getCorrectOption())) {
                correctAnswers++;
            }
            submission.addAnswer(new UserAnswer(question, input.selectedOption()));
        }
        if (submission.getAnswers().stream().map(answer -> answer.getQuestion().getId()).distinct().count() != 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each question must be answered once");
        }
        submission.setMarks(30, correctAnswers, 30 - correctAnswers);
        Submission savedSubmission = submissionRepository.save(submission);

        assignment.setStatus(com.kspiders.app.onlineexamportal.entity.Assignment.AssignmentStatus.COMPLETED);
        assignmentRepository.save(assignment);

        return savedSubmission;
    }

    public record AnswerInput(Long questionId, String selectedOption) {
    }
}
