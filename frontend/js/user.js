// The dashboard uses the token to load the current user's approval and assignment.
const token = sessionStorage.getItem('authToken');
const message = document.getElementById('dashboard-message');
const status = document.getElementById('approval-status');
const assignedSet = document.getElementById('assigned-set');
const startButton = document.getElementById('start-assessment');

if (!token) {
    window.location.href = 'index.html';
} else {
    loadUserStatus();
}

document.getElementById('logout').addEventListener('click', () => {
    sessionStorage.removeItem('authToken');
    window.location.href = 'index.html';
});

async function loadUserStatus() {
    const response = await fetch('/api/user/me', { headers: { 'X-Auth-Token': token } });
    if (!response.ok) {
        sessionStorage.removeItem('authToken');
        window.location.href = 'index.html';
        return;
    }

    // The backend decides whether the user is approved and has an assigned set.
    const user = await response.json();
    document.getElementById('welcome').textContent = `Welcome, ${user.fullName}`;
    status.textContent = user.approvalStatus;
    assignedSet.textContent = user.assignedSet || 'Not assigned';
    status.className = `status-${user.approvalStatus.toLowerCase()}`;

    if (user.approvalStatus === 'APPROVED' && user.assignedSet) {
        message.textContent = 'Your assessment is ready.';
        startButton.disabled = false;
        startButton.addEventListener('click', () => { window.location.href = 'assessment.html'; });
    } else if (user.approvalStatus === 'REJECTED') {
        message.textContent = 'Your access request was rejected. Contact the administrator.';
    } else {
        message.textContent = 'Your account is waiting for administrator approval and question-set assignment.';
    }
}
