package com.kspiders.app.onlineexamportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA Entity mapping an individual option selected by a candidate for a single question in a submission.
 */
@Entity
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Parent test submission instance. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    /** Target question entity being answered. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** Option selected by candidate ('A', 'B', 'C', or 'D'). */
    @Column(nullable = false, length = 1)
    private String selectedOption;

    /** Default constructor for JPA. */
    protected UserAnswer() {
    }

    /**
     * Parameterized constructor for UserAnswer creation.
     *
     * @param question       Answered question entity.
     * @param selectedOption Candidate's selected option key.
     */
    public UserAnswer(Question question, String selectedOption) {
        this.question = question;
        this.selectedOption = selectedOption;
    }

    public void setSubmission(Submission submission) { this.submission = submission; }
    public Long getId() { return id; }
    public Question getQuestion() { return question; }
    public String getSelectedOption() { return selectedOption; }
}
