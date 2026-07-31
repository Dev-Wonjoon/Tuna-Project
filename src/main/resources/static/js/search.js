const searchModal = document.getElementById("searchModal");
const searchOpen = document.getElementById("searchOpen");
const searchClose = document.getElementById("searchClose");

function openSearchModal() {
    searchModal.classList.remove("hidden");
    searchModal.classList.add("flex");
}

function closeSearchModal() {
    searchModal.classList.remove("flex");
    searchModal.classList.add("hidden");
}

searchOpen.addEventListener("click", openSearchModal);
searchClose.addEventListener("click", closeSearchModal);

// 팝업 바깥 영역 클릭 시 닫기
searchModal.addEventListener("click", (event) => {
    if (event.target === searchModal) {
        closeSearchModal();
    }
});

// ESC 키 입력 시 닫기
document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        closeSearchModal();
    }
});