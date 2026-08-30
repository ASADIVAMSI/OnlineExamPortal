package com.kspiders.app.onlineexamportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity mapping portal users (Students and Administrators) with account state, role, and registration approval status.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Candidate's full name. */
    @Column(nullable = false)
    private String fullName;

    /** Unique registration email address. */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt hashed account password. */
    @Column(nullable = false)
    private String password;

    /** System role assigned to the user (ADMIN or USER). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /** Admin approval lifecycle status for registration. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /** Default constructor for JPA. */
    protected User() {
    }

    /**
     * Parameterized constructor for User creation during registration.
     *
     * @param fullName User's full name.
     * @param email    Email address.
     * @param password Encoded password.
     */
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /** User roles within the application. */
    public enum Role {
        ADMIN, USER
    }

    /** Account registration approval statuses. */
    public enum ApprovalStatus {
        PENDING, PENDING_APPROVAL, APPROVED, REJECTED
    }
}
