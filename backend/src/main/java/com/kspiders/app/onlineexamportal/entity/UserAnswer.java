package com.kspiders.app.onlineexamportal.entity;

// A UserAnswer links one selected option to a question within a submission.

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 1)
    private String selectedOption;

    protected UserAnswer() {
    }

    public UserAnswer(Question question, String selectedOption) {
        this.question = question;
        this.selectedOption = selectedOption;
    }

    public void setSubmission(Submission submission) { this.submission = submission; }
    public Long getId() { return id; }
    public Question getQuestion() { return question; }
    public String getSelectedOption() { return selectedOption; }
}
