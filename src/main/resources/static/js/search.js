const search = () => {
    const keyword = document.getElementById("keyword").value.trim();

    if (keyword === "") {
        alert("검색어를 입력해주세요.");
        return;
    }

    window.location.href = '/search?keyword='.concat(keyword);
}
