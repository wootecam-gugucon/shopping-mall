const addCartItem = (productId) => {
    const credentials = sessionStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    // TODO: [3단계] 장바구니 아이템 추가 스펙에 맞게 변경
    fetch('', {
        method: '',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
    }).then((response) => {
        alert('장바구니에 담았습니다.');
    }).catch((error) => {
        console.error(error);
    });
}

const updateCartItemQuantity = (id, quantity) => {
    const credentials = sessionStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    // TODO: [3단계] 장바구니 아이템 수량 변경 스펙에 맞게 변경
    fetch('', {
        method: '',
        headers: {
            'Authorization': `Bearer ${credentials}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
    }).then((response) => {
        window.location.reload();
    }).catch((error) => {
        console.error(error);
    });
}

const removeCartItem = (id) => {
    const credentials = sessionStorage.getItem('accessToken');
    if (!credentials) {
        alert('사용자 정보가 없습니다.');
        window.location.href = '/login';
        return;
    }

    // TODO: [3단계] 장바구니 아이템 삭제 스펙에 맞게 변경
    fetch('', {
        method: '',
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
