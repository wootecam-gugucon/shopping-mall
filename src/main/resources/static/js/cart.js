const addCartItem = (productId) => {
    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch('/api/v1/cart/items', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"productId": productId})
    }).then((response) => {
        if (response.ok) {
            alert('장바구니에 담았습니다.');
        }
        else {
            response.json().then((data) => {alert(data.message)});
        }
    }).catch((error) => {
        console.error(error);
    });
}

const updateCartItemQuantity = (id, quantity) => {
    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/cart/items/${id}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"quantity": quantity})
    }).then((response) => {
        window.location.reload();
    }).catch((error) => {
        console.error(error);
    });
}

const removeCartItem = (id) => {
    const credentials = localStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    fetch(`/api/v1/cart/items/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        }
    }).then((response) => {
        window.location.reload();
    }).catch((error) => {
        console.error(error);
    });
}
