const getPointBalance = (elementId) => {
  const credentials = localStorage.getItem('accessToken');
  if (!credentials) {
    alert('사용자 정보가 없습니다.');
    window.location.href = '/login';
    return;
  }

  fetch(`/api/v1/point`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${credentials}`,
      'Content-Type': 'application/json'
    }
  }).then((response) => {
    return response.json();
  }).then((data) => {
    document.getElementById(elementId).innerText = data.point + ' P';
  }).catch((error) => {
    console.error(error);
  });
}
