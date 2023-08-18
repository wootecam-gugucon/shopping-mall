localStorage.removeItem('accessToken');
const form = document.getElementById('login-form');

form.addEventListener('submit', (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    let loginRequest = {};
    for (const entry of formData.entries()) {
        const [key, value] = entry;
        loginRequest[key] = value;
    }

    fetch('/api/v1/login/token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
    }).then((response) => {
        return response.json();
    }).then((data) => {
        const {accessToken} = data;
        localStorage.setItem('accessToken', accessToken);
        window.location.href = '/';
    }).catch((error) => {
        alert(error);
    });

});
