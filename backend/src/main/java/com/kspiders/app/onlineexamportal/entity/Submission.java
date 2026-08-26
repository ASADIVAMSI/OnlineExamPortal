package com.kspiders.app.onlineexamportal.entity;

// A Submission stores the result summary and owns the user's individual answers.

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

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private int totalMarks;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private int wrongAnswers;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> answers = new ArrayList<>();

    protected Submission() {
    }

    public Submission(User user, QuestionSet questionSet) {
        this.user = user;
        this.questionSet = questionSet;
        this.submittedAt = LocalDateTime.now();
    }

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

    public void addAnswer(UserAnswer answer) {
        answers.add(answer);
        answer.setSubmission(this);
    }

    public enum SubmissionStatus {
        SUBMITTED
    }
}
