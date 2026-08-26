// The token identifies the signed-in user for every protected API request.
const token = sessionStorage.getItem('authToken');
const form = document.getElementById('assessment-form');
const submitButton = document.getElementById('submit-assessment');
const message = document.getElementById('assessment-message');

if (!token) window.location.href = 'index.html';
document.getElementById('logout').addEventListener('click', () => {
    sessionStorage.removeItem('authToken');
    window.location.href = 'index.html';
});

loadQuestions();

async function loadQuestions() {
    // The backend returns only the question set assigned to this approved user.
    const response = await fetch('/api/assessment/questions', { headers: { 'X-Auth-Token': token } });
    if (!response.ok) {
        message.textContent = response.status === 401
            ? 'Your session expired. Please sign in again.'
            : 'Assessment access requires admin approval and a question-set assignment.';
        if (response.status === 401) sessionStorage.removeItem('authToken');
        return;
    }
    const assessment = await response.json();
    document.getElementById('set-name').textContent = assessment.questionSetName;
    form.replaceChildren(...assessment.questions.map(createQuestion));
}

function createQuestion(question, index) {
    const article = document.createElement('article');
    article.className = 'question-card';
    article.dataset.questionId = question.id;
    article.innerHTML = `<h2>${index + 1}. ${question.questionText}</h2><div class="answer-options">
        <label><input type="radio" name="question-${question.id}" value="A"> ${question.optionA}</label>
        <label><input type="radio" name="question-${question.id}" value="B"> ${question.optionB}</label>
        <label><input type="radio" name="question-${question.id}" value="C"> ${question.optionC}</label>
        <label><input type="radio" name="question-${question.id}" value="D"> ${question.optionD}</label>
    </div>`;
    article.querySelectorAll('input').forEach((input) => input.addEventListener('change', updateCompletion));
    return article;
}

function updateCompletion() {
    // The submit button is enabled only when all 30 questions have an answer.
    const answered = new Set([...form.querySelectorAll('input:checked')].map((input) => input.name));
    const total = form.querySelectorAll('.question-card').length;
    submitButton.disabled = answered.size !== total || total !== 30;
    message.textContent = `${answered.size} of ${total} questions answered.`;
}

form.addEventListener('submit', (event) => {
    event.preventDefault();
    submitAnswers();
});

async function submitAnswers() {
    submitButton.disabled = true;
    const answers = [...form.querySelectorAll('.question-card')].map((card) => {
        const selected = card.querySelector('input:checked');
        return { questionId: Number(card.dataset.questionId), selectedOption: selected?.value };
    });
    if (answers.some((answer) => !answer.selectedOption)) {
        message.textContent = 'Please answer all 30 questions before submitting.';
        submitButton.disabled = false;
        return;
    }
    try {
        // Send the selected option for each question and let the backend calculate marks.
        const response = await fetch('/api/assessment/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-Auth-Token': token },
            body: JSON.stringify({ answers })
        });
        const result = await response.json().catch(() => ({}));
        if (!response.ok) {
            message.textContent = response.status === 401
                ? 'Your session expired. Please sign in again.'
                : result.detail || result.message || 'Submission failed. Please try again.';
            submitButton.disabled = false;
            return;
        }
        message.textContent = `Assessment submitted successfully at ${new Date(result.submittedAt).toLocaleString()}.`;
        form.querySelectorAll('input').forEach((input) => { input.disabled = true; });
        submitButton.textContent = 'Submitted';
    } catch (error) {
        message.textContent = 'The server is unavailable. Please restart the Java backend and sign in again.';
        submitButton.disabled = false;
    }
}
