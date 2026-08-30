package com.kspiders.app.onlineexamportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity representing a Question Set module containing grouped assessment questions.
 */
@Entity
@Table(name = "question_sets")
public class QuestionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique display name of the question set (e.g. "Question Set 1 - 30 Questions"). */
    @Column(nullable = false, unique = true)
    private String name;

    /** Topic description or syllabus covered by this set. */
    @Column(nullable = false)
    private String description;

    /** Default constructor required by JPA. */
    protected QuestionSet() {
    }

    /**
     * Parameterized constructor for QuestionSet creation.
     *
     * @param name        Question set name.
     * @param description Module description.
     */
    public QuestionSet(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
