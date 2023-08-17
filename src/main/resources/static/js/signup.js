
const form = document.getElementById('signup-form');

form.addEventListener('submit', (event) => {
  event.preventDefault();

  const formData = new FormData(event.target);
  let signupRequest = {};
  for (const entry of formData.entries()) {
    const [key, value] = entry;
    signupRequest[key] = value;
  }

  if (signupRequest["password2"] !== signupRequest["password"]) {
    alert("비밀번호가 일치하지 않습니다.");
    return false;
  }

  fetch('/api/v1/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(signupRequest)
  }).then((response) => {
    if (response.status === 201) {
      alert("회원가입이 완료되었습니다. 로그인해 주세요.");
      window.location.href = '/login';
    }
  }).catch((error) => {
    alert(error);
  });

});
