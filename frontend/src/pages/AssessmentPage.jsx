import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { assessmentAPI } from '../services/assessmentApi.js';
import './AssessmentPage.css';

/**
 * Assessment Taking Interface Page Component.
 * Fetches assigned 30 questions, tracks candidate option selections, validates full submission completeness,
 * and handles exam submission to backend.
 */
function AssessmentPage() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [questionSetName, setQuestionSetName] = useState('Loading questions...');
  const [answers, setAnswers] = useState({});
  const [message, setMessage] = useState('Answer every question before submitting.');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitted, setIsSubmitted] = useState(false);

  useEffect(() => {
    // Authenticate candidate session before loading test
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      navigate('/');
      return;
    }

    loadQuestions();
  }, [navigate]);

  useEffect(() => {
    if (questions.length === 0) {
      return;
    }

    const answered = Object.keys(answers).length;
    if (answered === questions.length) {
      setMessage('All questions are answered. You can submit the assessment now.');
    } else {
      setMessage(`Answer every question before submitting. ${answered}/${questions.length} complete.`);
    }
  }, [answers, questions.length]);

  const loadQuestions = async () => {
    try {
      setIsLoading(true);
      setErrorMessage('');
      const data = await assessmentAPI.getQuestions();
      setQuestions(data.questions);
      setQuestionSetName(data.questionSetName);
      setMessage('Answer every question before submitting.');
      setIsSubmitted(false);
      setAnswers({});
    } catch (error) {
      setQuestions([]);
      if (error.status === 401) {
        setMessage('Your session expired. Please sign in again.');
        sessionStorage.removeItem('authToken');
      } else {
        setMessage('Assessment access requires admin approval and a question-set assignment.');
      }
      setErrorMessage(error.message || '');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    sessionStorage.removeItem('authToken');
    navigate('/');
  };

  const handleAnswerChange = (questionId, option) => {
    setAnswers((prev) => ({ ...prev, [questionId]: option }));
  };

  const answeredCount = Object.keys(answers).length;
  const expectedQuestionCount = questions.length || 30;
  const isFormComplete = questions.length > 0 && answeredCount === questions.length;

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (questions.length !== 30) {
      setMessage('The assigned assessment must contain exactly 30 questions. Please refresh and try again.');
      return;
    }

    if (!isFormComplete) {
      setMessage(`Please answer all ${questions.length} questions before submitting.`);
      return;
    }

    setIsSubmitting(true);

    try {
      const answersList = questions.map((question) => ({
        questionId: question.id,
        selectedOption: answers[question.id]
      }));

      const result = await assessmentAPI.submitAssessment(answersList);
      setMessage(`Assessment submitted successfully at ${new Date(result.submittedAt).toLocaleString()}.`);
      setIsSubmitted(true);
    } catch (error) {
      if (error.status === 401) {
        setMessage('Your session expired. Please sign in again.');
        sessionStorage.removeItem('authToken');
      } else {
        setMessage(error.message || 'Submission failed. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="assessment-page">
      <header className="site-header page-frame">
        <Link className="brand" to="/user-dashboard">
          <span className="brand-mark">+</span>
          <span>Online Exam Portal</span>
        </Link>
        <button className="outline-button" onClick={handleLogout} type="button">
          Sign out
        </button>
      </header>

      <main className="assessment-shell page-frame">
        <section className="assessment-hero section-card">
          <p className="eyebrow">Assigned assessment</p>
          <h1 className="panel-title" id="set-name">{questionSetName}</h1>
          <p id="assessment-message" className="intro-copy">{message}</p>
          <div className="assessment-meta">
            <span className="meta-chip">{answeredCount} / {expectedQuestionCount} answered</span>
            <span className="meta-chip">{isSubmitted ? 'Submitted' : 'In progress'}</span>
          </div>
          <div className="progress-track" aria-hidden="true">
            <span className="progress-bar" style={{ width: `${(answeredCount / expectedQuestionCount) * 100 || 0}%` }} />
          </div>
        </section>

        {isLoading ? (
          <div className="loading">Loading questions...</div>
        ) : questions.length === 0 ? (
          <div className="error-state">
            <p>{message}</p>
            {errorMessage && <p className="error-copy">{errorMessage}</p>}
            <button className="primary-button" type="button" onClick={loadQuestions}>
              Retry
            </button>
          </div>
        ) : (
          <form id="assessment-form" className="question-list" onSubmit={handleSubmit}>
            {questions.map((question, index) => (
              <article key={question.id} className="question-card section-card">
                <h2>{index + 1}. {question.questionText}</h2>
                <div className="answer-options">
                  <label>
                    <input type="radio" name={`question-${question.id}`} value="A" checked={answers[question.id] === 'A'} onChange={(e) => handleAnswerChange(question.id, e.target.value)} />
                    <span className="option-key">A</span>
                    <span className="option-text">{question.optionA}</span>
                  </label>
                  <label>
                    <input type="radio" name={`question-${question.id}`} value="B" checked={answers[question.id] === 'B'} onChange={(e) => handleAnswerChange(question.id, e.target.value)} />
                    <span className="option-key">B</span>
                    <span className="option-text">{question.optionB}</span>
                  </label>
                  <label>
                    <input type="radio" name={`question-${question.id}`} value="C" checked={answers[question.id] === 'C'} onChange={(e) => handleAnswerChange(question.id, e.target.value)} />
                    <span className="option-key">C</span>
                    <span className="option-text">{question.optionC}</span>
                  </label>
                  <label>
                    <input type="radio" name={`question-${question.id}`} value="D" checked={answers[question.id] === 'D'} onChange={(e) => handleAnswerChange(question.id, e.target.value)} />
                    <span className="option-key">D</span>
                    <span className="option-text">{question.optionD}</span>
                  </label>
                </div>
              </article>
            ))}

            <div className="assessment-footer section-card">
              <div className="intro-copy">
                Every question must be answered before submission is enabled.
              </div>
              <button
                id="submit-assessment"
                className="primary-button"
                type="submit"
                disabled={!isFormComplete || isSubmitting || isSubmitted}
              >
                {isSubmitted ? 'Submitted' : isSubmitting ? 'Submitting...' : 'Submit assessment'}
              </button>
            </div>
          </form>
        )}
      </main>
    </div>
  );
}

export default AssessmentPage;
