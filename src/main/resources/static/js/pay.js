const payPopUp = (orderId) => {

    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/pay`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"orderId": orderId})
    }).then((response) => {
        return response.json();
    }).then((data) => {
        const url = `/pay/${data.payId}`;
        const name = "payment popup";
        const option = "width = 600, height = 800, top = 100, left = 200, location = no";
        window.open(url, name, option);
    }).catch((error) => {
        console.error(error);
    });
}
