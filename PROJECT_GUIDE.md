Absolutely. I would organize it as a **professional `PROJECT_GUIDE.md`** while keeping all the original requirements intact.

# OnlineExamPortal — Project Guide

## 1. Project Overview

**OnlineExamPortal** is a Java-based online assessment application.

The application should provide a complete workflow where users can register and log in, receive approval from an administrator, get an assigned question set, attend the assessment, submit their answers, and allow the administrator to view the submitted answers and assessment details.

The **frontend, backend, and database must be connected and work together as one complete application**.

---

## 2. Project Technology

The technologies can be selected based on the development team's understanding and requirements.

### Backend

* Java
* Any suitable Java backend technology

### Frontend

* Any suitable frontend technology

### Database

* Any suitable database

### Required Integration

The following components must work together:

```text
Frontend
    ↓
Backend
    ↓
Database
```

---

# 3. User Roles and Login

The application must support **two types of users**:

1. Admin
2. User

---

## 3.1 Admin Login

The Admin should be able to:

* Log in securely.
* View registered users.
* Manage user assessment access.
* Approve users before they can attend an assessment.
* Assign question sets to users or groups.
* View submitted answers.
* View assessment submission details.

---

## 3.2 User Login

Users should be able to:

* Register/login to the application.
* View their assessment status.
* Wait for Admin approval before accessing an assessment.
* Attend the question set assigned to them.
* Submit their answers after completing the assessment.

### Important Rule

A user **must not be allowed to attend an assessment immediately after login**.

The user must first receive approval from the Admin.

---

# 4. Question Sets

The application must contain **4 different question sets**.

Each question set must contain exactly:

> **30 programming-language questions**

### Question Types

The questions should include:

* Multiple-choice questions
* Theory questions
* Coding questions

### Question Requirements

* Each set must contain exactly 30 questions.
* Questions must not be repeated between different sets.
* Each set should cover programming basics and important concepts.
* A user should receive only the question set assigned by the Admin.

### Question Set Structure

```text
Question Set 1 → 30 Questions
Question Set 2 → 30 Questions
Question Set 3 → 30 Questions
Question Set 4 → 30 Questions
```

There should be **120 questions in total**, with no repeated questions between sets.

---

# 5. User Groups and Question Set Assignment

Users can be divided into any number of groups.

The Admin should be able to:

* Create/manage user groups.
* Assign a question set to a user.
* Assign a question set to a group.
* View which question set is assigned to each user.

### Important Rule

Users **must not be allowed to choose their own question set**.

The question set must be determined by the Admin's assignment.

---

# 6. Assessment Workflow

The complete application workflow should be:

```text
User Login
     ↓
Admin Approval
     ↓
Question Set Assignment
     ↓
User Starts Assessment
     ↓
User Attempts 30 Questions
     ↓
User Submits Assessment
     ↓
Answers Are Saved
     ↓
Admin Views Submission
```

### Access Control

A user should only be able to start the assessment when:

* The user has successfully logged in.
* The Admin has approved the user.
* A question set has been assigned to the user.

---

# 7. Assessment

Once the user is approved and receives a question set:

* The assigned question set should be displayed.
* The user should receive exactly 30 questions.
* The user should not be able to select another question set.
* The user must answer all 30 questions.
* The user can submit the assessment only after answering all 30 questions.

### Submission Rule

The system should prevent submission if any of the 30 questions remain unanswered.

---

# 8. Assessment Submission

After completing all 30 questions, the user should click the **Submit** button.

When the user submits:

1. The system should save the user's answers.
2. The system should save the submission details.
3. The submission status should be recorded.
4. The submission date/time should be recorded.
5. The submitted answers should become available to the Admin.

---

# 9. Admin Submission View

The Admin should be able to view submitted assessment information.

The Admin should be able to see:

| Information       | Description                                   |
| ----------------- | --------------------------------------------- |
| User Name         | Name of the user who submitted the assessment |
| Question Set      | Question set assigned to the user             |
| Answers           | Answers submitted by the user                 |
| Submission Status | Current submission status                     |
| Submission Date   | Date/time when the assessment was submitted   |

---

# 10. Database Requirements

The database should store all information required for the application.

The database should contain information related to:

### Admin

* Admin details
* Login information

### Users

* User details
* Login information
* Approval status

### Question Sets

* Question set details
* Questions
* Question type

### Assignment

* User/group
* Assigned question set

### User Answers

* User
* Question
* Selected/submitted answer

### Submission

* User
* Question set
* Submission status
* Submission date/time

---

# 11. Important Functional Requirements

The application must satisfy all of the following requirements:

* Users cannot access the assessment without Admin approval.
* Each user must receive the correct question set assigned by the Admin.
* Users cannot choose a different question set.
* There must be exactly 4 question sets.
* Each question set must contain exactly 30 questions.
* Questions must not be repeated between question sets.
* Each question set should contain programming-related multiple-choice, theory, and coding questions.
* All 30 questions must be answered before submission.
* User answers must be stored in the database.
* Submission details must be stored.
* Submitted answers must be visible to the Admin.
* The complete workflow must work from login through final submission.

---

# 12. Complete Application Flow

### Admin Flow

```text
Admin Login
     ↓
View Users
     ↓
Approve User
     ↓
Assign Question Set
     ↓
User Attends Assessment
     ↓
User Submits Assessment
     ↓
Admin Views Submission
```

### User Flow

```text
User Login
     ↓
Check Approval Status
     ↓
Wait for Admin Approval
     ↓
Receive Assigned Question Set
     ↓
Start Assessment
     ↓
Answer 30 Questions
     ↓
Submit Assessment
     ↓
Submission Saved
```

---

# 13. Required Project Structure

Keep the application in three separate top-level folders named `frontend`, `backend`, and `database`.
The Java backend package structure should follow the package layout shown in the reference project.

```text
OnlineExamPortal/
├── frontend/
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── user-dashboard.html
│   ├── assessment.html
│   ├── result.html
│   ├── admin-dashboard.html
│   ├── manage-users.html
│   ├── manage-groups.html
│   ├── manage-question-sets.html
│   ├── view-submissions.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       ├── auth.js
│       ├── user.js
│       ├── admin.js
│       └── assessment.js
│
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/kspiders/app/onlineexamportal/
│       │   ├── OnlineExamPortalApplication.java
│       │   ├── configuration/
│       │   │   └── SecurityConfiguration.java
│       │   ├── dao/
│       │   │   ├── UserRepository.java
│       │   │   ├── GroupRepository.java
│       │   │   ├── QuestionSetRepository.java
│       │   │   ├── QuestionRepository.java
│       │   │   ├── AssignmentRepository.java
│       │   │   ├── SubmissionRepository.java
│       │   │   └── UserAnswerRepository.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Role.java
│       │   │   ├── UserGroup.java
│       │   │   ├── QuestionSet.java
│       │   │   ├── Question.java
│       │   │   ├── Assignment.java
│       │   │   ├── Submission.java
│       │   │   └── UserAnswer.java
│       │   ├── resource/
│       │   │   ├── AuthResource.java
│       │   │   ├── UserResource.java
│       │   │   ├── AdminResource.java
│       │   │   ├── QuestionSetResource.java
│       │   │   └── AssessmentResource.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       ├── UserService.java
│       │       ├── AdminService.java
│       │       ├── QuestionSetService.java
│       │       └── AssessmentService.java
│       ├── main/resources/
│       │   └── application.properties
│       └── test/java/com/codegnan/app/onlineexamportal/
│           ├── AuthServiceTest.java
│           └── AssessmentServiceTest.java
│
├── database/
│   ├── schema.sql
│   ├── seed_admin.sql
│   └── seed_questions.sql
│
├── PROJECT_GUIDE.md
├── README.md
└── .gitignore
```

Do not put SQL files inside `frontend` or Java source files inside `database`. The frontend calls backend API endpoints, and the backend reads and writes the database.

## 13.1 Development Sequence

Develop the project in this order:

1. Create the `frontend`, `backend`, and `database` folders.
2. Create the Spring Boot Maven project inside `backend`.
3. Add Web, JPA, MySQL, Validation, and Security dependencies to `backend/pom.xml`.
4. Create the database and write `database/schema.sql`.
5. Configure the database connection in `backend/src/main/resources/application.properties`.
6. Create the entity classes and DAO repositories.
7. Implement registration, login, password encryption, and role-based access.
8. Implement Admin approval and user/group management.
9. Insert four sets containing 30 unique questions each in `database/seed_questions.sql`.
10. Implement Admin question-set assignment to users and groups.
11. Build the user dashboard and enforce approval and assignment checks in the backend.
12. Build the assessment page and require all 30 answers before submission.
13. Save submissions and user answers, then build the Admin submission view.
14. Connect frontend pages to backend APIs using JavaScript `fetch` calls.
15. Test the complete workflow and document setup steps in `README.md`.

---

# 14. GitHub Repository Requirements

The complete project must be uploaded to a separate GitHub repository.

### Repository Name

```text
OnlineExamPortal
```

### Repository Should Include

* Frontend source code
* Backend source code
* Database scripts/SQL file
* `README.md`
* `PROJECT_GUIDE.md`
* Relevant dependency files
* `pom.xml` or equivalent backend dependency file
* Frontend dependency files, if applicable
* Clear setup and execution instructions
* Screenshots, if required

---

# 15. README Requirements

The `README.md` file should explain:

* Project overview
* Technologies used
* Features
* Project structure
* Database setup
* Backend setup
* Frontend setup
* How to run the application
* Default/admin login details, if applicable
* How frontend and backend communicate
* How to access the application

---

# 16. Final Project Objective

The main objective of **OnlineExamPortal** is to understand how the following components work together in a real-world application:

```text
Java Backend
     +
Frontend
     +
Database
     +
Authentication
     +
User Management
     +
Admin Management
     +
Question Management
     +
Assessment Workflow
     +
Answer Submission
```

The final application should provide a complete working flow from **user login → Admin approval → question-set assignment → assessment → answer submission → Admin review**.

---

# 17. Final Verification Checklist

Before submitting the project, verify that:

* [ ] Admin login works.
* [ ] User login works.
* [ ] Admin can view users.
* [ ] Admin can approve users.
* [ ] Unapproved users cannot start an assessment.
* [ ] Admin can assign question sets.
* [ ] Users cannot choose their own question set.
* [ ] Four question sets are available.
* [ ] Each question set contains exactly 30 questions.
* [ ] Questions are not repeated between sets.
* [ ] Multiple-choice questions are included.
* [ ] Theory questions are included.
* [ ] Coding questions are included.
* [ ] Users can attempt their assigned assessment.
* [ ] All 30 questions are required before submission.
* [ ] User answers are saved.
* [ ] Submission status is saved.
* [ ] Submission date/time is saved.
* [ ] Admin can view submitted answers.
* [ ] Frontend, backend, and database are connected.
* [ ] The complete application works from login to final submission.
* [ ] All required files are uploaded to GitHub.
* [ ] `README.md` is included.
* [ ] Database SQL/scripts are included.
* [ ] Project setup instructions are included.
* [ ] GitHub repository is accessible.
* [ ] Final GitHub repository link is ready for submission.
