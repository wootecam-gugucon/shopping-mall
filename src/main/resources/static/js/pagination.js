const pagination = (currentPage, totalPage, size, className) => {

    if (totalPage > 1) {
        const pageCount = 5;
        const pageGroup = Math.ceil((currentPage + 1) / pageCount);
        const last = (pageCount * pageGroup) > totalPage ? totalPage : pageCount
            * pageGroup;
        const first = pageCount * (pageGroup - 1) + 1;

        let pages = '';
        pages += `
          <button id="page-${first - 1}" onclick="location.href='/?page=${first - 2}&size=${size}'">이전</button>
        `;
        for (let i = first; i <= last; i++) {
            pages += `
            <button id="page-${i}" onclick="location.href='/?page=${i - 1}&size=${size}'">${i}</button>
          `;
        }
        pages += `
          <button id="page-${last + 1}" onclick="location.href='/?page=${last}&size=${size}'">다음</button>
        `;
        document.querySelector(className).innerHTML = pages;
        document.getElementById(`page-${currentPage + 1}`).classList.add("active");
        if (last >= totalPage) {
            const next = document.getElementById(`page-${last + 1}`);
            next.setAttribute("disabled", true);
        }
        if (first - 1 <= 0) {
            const prev = document.getElementById(`page-${first - 1}`);
            prev.setAttribute("disabled", true);
        }
    }
}
