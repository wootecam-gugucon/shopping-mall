const form = document.getElementById('search-form');

form.addEventListener('submit', (event) => {
    event.preventDefault();

    const searchInput = document.getElementById("searchInput").value;
    const sortSelect = document.getElementById("sortSelect");
    const selectedSort = sortSelect.options[sortSelect.selectedIndex].value;
    let queryParams = `keyword=${searchInput}&sort=${selectedSort}`;

    if (selectedSort === "orderCount,desc" || selectedSort === "rate,desc") {
        const gender = document.getElementById("gender").value;
        const birthYearRange = document.getElementById("birthYearRange").value;
        if (gender && birthYearRange) {
            queryParams += `&gender=${gender}&birthYearRange=${birthYearRange}`;
        }
        else if (gender || birthYearRange) {
            alert("성별과 나이대 모두 선택해주세요.");
        }
    }

    window.location.href = `/search?${queryParams}`;
});

