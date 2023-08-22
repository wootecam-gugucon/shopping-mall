const form = document.getElementById('point-form');

form.addEventListener('submit', (event) => {
  event.preventDefault();

  const credentials = localStorage.getItem('accessToken');
  if (!credentials) {
    alert('사용자 정보가 없습니다.');
    window.location.href = '/login';
    return;
  }

  const formData = new FormData(event.target);
  let chargeRequest = {};
  for (const entry of formData.entries()) {
    const [key, value] = entry;
    chargeRequest[key] = value;
  }
  console.log(chargeRequest);
  console.log(JSON.stringify(chargeRequest));

  fetch('/api/v1/point', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${credentials}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(chargeRequest)
  }).then((response) => {
    if (response.ok) {
      alert("충전이 완료되었습니다.")
      window.location.reload();
    }
    else response.json().then((data) => alert(data.message));
  }).catch((error) => {
    alert(error);
  });

});