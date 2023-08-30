const requestOrder = () => {
    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch('/api/v1/order', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        }
    }).then((response) => {
        if (response.ok) {
            window.location.href = response.headers.get('Location');
        }
        else {
            response.json().then((data) => {
                alert(data.message);
            });
        }
    }).catch((error) => {
        console.error(error);
    });
}
