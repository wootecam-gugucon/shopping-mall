const putOrder = (orderId, type) => {

    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/order`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "orderId": orderId,
            "payType": type,
        })
    }).then((response) => {
        if (response.ok) {
            if (type === 'POINT') {
                pointOrder(orderId);
            }
            else if (type === 'TOSS') {
                tossOrder(orderId);
            }
        }
        else {
            response.json().then((data) => alert(data.message));
        }
    }).catch((error) => {
        console.error(error);
    });
}

const tossOrder = (orderId) => {

    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/pay`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "orderId": orderId,
            "payType": "TOSS"
        })
    }).then((response) => {
        return response.json();
    }).then((data) => {
        const url = `/pay/${data.orderId}`;
        const name = "payment popup";
        const option = "width = 600, height = 800, top = 100, left = 200, location = no";
        window.open(url, name, option);
    }).catch((error) => {
        console.error(error);
    });
}


const pointOrder = (orderId) => {

    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/pay`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "orderId": orderId,
            "payType": "POINT"
        })
    }).then((response) => {
        if (response.ok) {
            response.json().then((data) => {
                window.location.href = `/pay/success?orderId=${data.orderId}`;
            });
        }
        else {
            response.json().then((data) => alert(data.message));
        }
    }).catch((error) => {
        console.error(error);
    });
}
