function openLogoutModal() {
    const modal = document.getElementById("logoutModal");

    modal.classList.remove("hidden");
    modal.classList.add("flex");
}

function closeLogoutModal() {
    const modal = document.getElementById("logoutModal");

    modal.classList.remove("flex");
    modal.classList.add("hidden");
}