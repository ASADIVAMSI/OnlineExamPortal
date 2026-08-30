package com.kspiders.app.onlineexamportal.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a completed candidate exam submission, storing scores, accuracy stats, and child user answers.
 */
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who took the exam. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Question set completed during this test session. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    /** Submission status (SUBMITTED). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    /** Timestamp when submission occurred. */
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    /** Total score achieved. */
    @Column(nullable = false)
    private int totalMarks;

    /** Total correct answer count. */
    @Column(nullable = false)
    private int correctAnswers;

    /** Total incorrect answer count. */
    @Column(nullable = false)
    private int wrongAnswers;

    /** Cascade collection of individual question answers submitted by the user. */
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> answers = new ArrayList<>();

    /** Default constructor for JPA. */
    protected Submission() {
    }

    /**
     * Parameterized constructor for initiating a new test submission.
     *
     * @param user        Candidate user.
     * @param questionSet Question set evaluated.
     */
    public Submission(User user, QuestionSet questionSet) {
        this.user = user;
        this.questionSet = questionSet;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Helper method to set final calculated score metrics.
     *
     * @param totalMarks     Total marks awarded.
     * @param correctAnswers Total questions answered correctly.
     * @param wrongAnswers   Total questions answered incorrectly.
     */
    public void setMarks(int totalMarks, int correctAnswers, int wrongAnswers) {
        this.totalMarks = totalMarks;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public QuestionSet getQuestionSet() { return questionSet; }
    public SubmissionStatus getStatus() { return status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public int getTotalMarks() { return totalMarks; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public List<UserAnswer> getAnswers() { return answers; }

    /**
     * Helper method to attach an individual user answer to this submission.
     *
     * @param answer UserAnswer entity to link.
     */
    public void addAnswer(UserAnswer answer) {
        answers.add(answer);
        answer.setSubmission(this);
    }

    /** Enumeration for submission status. */
    public enum SubmissionStatus {
        SUBMITTED
    }
}
