const addCartItem = (productId) => {
  const credentials = sessionStorage.getItem('accessToken');
  if (!credentials) {
    alert('사용자 정보가 없습니다.');
    window.location.href = '/login';
    return;
  }

  fetch('/cart/items', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${credentials}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({"productId": productId})
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

  fetch(`/cart/items/${id}/quantity`, {
    method: 'PUT',
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
  const credentials = sessionStorage.getItem('accessToken');
  if (!credentials) {
    alert('사용자 정보가 없습니다.');
    window.location.href = '/login';
    return;
  }
  
  fetch(`/cart/items/${id}`, {
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
