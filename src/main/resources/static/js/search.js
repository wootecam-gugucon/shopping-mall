const search = () => {
    const keyword = document.getElementById("keyword").value.trim();

    if (keyword === "") {
        alert("검색어를 입력해주세요.");
        return;
    }

    window.location.href = '/search?keyword='.concat(keyword);
}

const selectSortKey = (sortKey) => {
    const buttons = document.getElementsByName("sort-select-button");
    let button;
    if (sortKey === null || sortKey === "") {
        button = buttons[0];
    } else if(sortKey === "price,desc") {
        button = buttons[1];
    } else if(sortKey === "price,asc") {
        button = buttons[2];
    } else {
        button = buttons[3];
    }
    button.style.fontWeight = "bold";
    button.style.color = "white";
    button.style.backgroundColor = "darkslategray";
}
