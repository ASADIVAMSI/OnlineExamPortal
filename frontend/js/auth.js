// Authentication requests are sent to the Spring Boot API.
const API_URL = 'http://localhost:8080/api/auth';
const message = document.getElementById('message');
const signinForm = document.getElementById('signin-form');
const signupForm = document.getElementById('signup-form');
const signinTab = document.getElementById('signin-tab');
const signupTab = document.getElementById('signup-tab');
const authSection = document.getElementById('auth-section');
const loginCards = [document.getElementById('user-login'), document.getElementById('admin-login')];

function openSignin(selectedCard) {
    authSection.classList.remove('hidden');
    loginCards.forEach((card) => card.classList.toggle('selected', card === selectedCard));
    switchForm(false);
    authSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// Both the header buttons and the main choice cards open the same sign-in form.
document.getElementById('user-login').addEventListener('click', () => openSignin(document.getElementById('user-login')));
document.getElementById('header-user-login').addEventListener('click', openSignin);
document.getElementById('admin-login').addEventListener('click', () => openSignin(document.getElementById('admin-login')));
document.getElementById('header-admin-login').addEventListener('click', openSignin);

function showMessage(text, isError = false) {
    message.textContent = text;
    message.classList.toggle('error', isError);
}

function switchForm(showSignup) {
    signupForm.classList.toggle('hidden', !showSignup);
    signinForm.classList.toggle('hidden', showSignup);
    signupTab.classList.toggle('active', showSignup);
    signinTab.classList.toggle('active', !showSignup);
    showMessage('');
}

signinTab.addEventListener('click', () => switchForm(false));
signupTab.addEventListener('click', () => switchForm(true));

async function sendAuthRequest(path, payload) {
    // Keeping fetch logic in one function gives sign-in and sign-up the same error handling.
    const response = await fetch(`${API_URL}/${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(data.detail || data.message || 'Request failed. Please try again.');
    }
    return data;
}

signupForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        const data = await sendAuthRequest('signup', {
            fullName: document.getElementById('signup-name').value,
            email: document.getElementById('signup-email').value,
            password: document.getElementById('signup-password').value
        });
        signupForm.reset();
        showMessage(data.message);
    } catch (error) {
        showMessage(error.message, true);
    }
});

signinForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        const data = await sendAuthRequest('signin', {
            email: document.getElementById('signin-email').value,
            password: document.getElementById('signin-password').value
        });
        sessionStorage.setItem('authToken', data.token);
        if (data.role === 'ADMIN') {
            window.location.href = 'admin.html';
            return;
        }
        window.location.href = 'user-dashboard.html';
    } catch (error) {
        showMessage(error.message, true);
    }
});
