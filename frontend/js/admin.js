// Admin API requests use the same session token created during sign-in.
const token = sessionStorage.getItem('authToken');
const usersElement = document.getElementById('users');
const messageElement = document.getElementById('admin-message');
const submissionsElement = document.getElementById('submissions');
const submissionCount = document.getElementById('submission-count');

if (!token) {
    messageElement.textContent = 'Please sign in as an admin first.';
} else {
    loadAdminData();
    setInterval(loadAdminData, 10000);
}

async function loadAdminData() {
    const headers = { 'X-Auth-Token': token };
    // Load users, available question sets, and submissions together for the admin view.
    const [usersResponse, setsResponse, submissionsResponse] = await Promise.all([
        fetch('/api/admin/users', { headers }),
        fetch('/api/admin/question-sets', { headers }),
        fetch('/api/admin/submissions', { headers })
    ]);
    if (!usersResponse.ok || !setsResponse.ok || !submissionsResponse.ok) {
        messageElement.textContent = 'Admin access is required.';
        return;
    }
    const users = await usersResponse.json();
    const sets = await setsResponse.json();
    const submissions = await submissionsResponse.json();
    usersElement.replaceChildren(...users.map((user) => createUserRow(user, sets)));
    submissionCount.textContent = submissions.length;
    submissionsElement.replaceChildren(...submissions.map(createSubmission));
}

function createSubmission(submission) {
    const card = document.createElement('article');
    card.className = 'submission-card';
    const questionResults = submission.answers.map((answer, index) => `<li class="${answer.correct ? 'answer-correct' : 'answer-wrong'}">${index + 1}. ${answer.correct ? 'Correct' : 'Wrong'} - selected ${answer.selectedOption}, correct ${answer.correctOption}</li>`).join('');
    card.innerHTML = `<div><strong>${submission.userName}</strong><span>${submission.email} / ${submission.questionSetName}</span><small>${new Date(submission.submittedAt).toLocaleString()}</small><details><summary>View answer review</summary><ol>${questionResults}</ol></details></div><div class="marks"><b>${submission.correctAnswers} / ${submission.totalMarks}</b><span>${submission.wrongAnswers} wrong</span></div>`;
    return card;
}

function createUserRow(user, sets) {
    const row = document.createElement('article');
    row.className = 'user-row';
    const details = document.createElement('div');
    details.innerHTML = `<strong>${user.fullName}</strong><span>${user.email}</span><small>${user.role} / ${user.approvalStatus}</small>`;
    row.appendChild(details);

    if (user.role !== 'ADMIN') {
        const actions = document.createElement('div');
        actions.className = 'actions';
        actions.appendChild(actionButton('Approve', 'approve', user.id));
        actions.appendChild(actionButton('Reject', 'reject', user.id));
        const setSelect = document.createElement('select');
        setSelect.className = 'set-select';
        setSelect.innerHTML = '<option value="">Assign set</option>' + sets
            .map((set) => `<option value="${set.id}">${set.name}</option>`).join('');
        setSelect.addEventListener('change', () => assignSet(user.id, setSelect.value));
        actions.appendChild(setSelect);
        row.appendChild(actions);
    }
    return row;
}

async function assignSet(userId, questionSetId) {
    if (!questionSetId) return;
    const response = await fetch(`/api/admin/users/${userId}/question-set/${questionSetId}`, {
        method: 'PUT',
        headers: { 'X-Auth-Token': token }
    });
    messageElement.textContent = response.ok ? 'Question set assigned successfully.' : 'The question set could not be assigned.';
}

function actionButton(label, action, userId) {
    const button = document.createElement('button');
    button.className = 'primary-button compact';
    button.textContent = label;
    button.addEventListener('click', async () => {
        const response = await fetch(`/api/admin/users/${userId}/${action}`, {
            method: 'PUT',
            headers: { 'X-Auth-Token': token }
        });
        if (response.ok) {
            messageElement.textContent = `User ${action}d successfully.`;
            await loadAdminData();
        } else {
            messageElement.textContent = 'The update could not be completed.';
        }
    });
    return button;
}
