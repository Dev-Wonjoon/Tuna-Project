function openLogoutModal() {
    const modal = document.getElementById("logoutModal");

    if(modal && !modal.open) {
        modal.showModal();
    }
}

function closeLogoutModal() {
    const modal = document.getElementById("logoutModal");

    if(modal?.open) {
        modal.close();
    }
}