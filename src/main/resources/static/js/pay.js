const payPopUp = (orderId, orderName, price) => {
    const url = `/pay?orderId=${orderId}&orderName=${orderName}&price=${price}`;
    const name = "payment popup";
    const option = "width = 600, height = 800, top = 100, left = 200, location = no"
    window.open(url, name, option);
}
