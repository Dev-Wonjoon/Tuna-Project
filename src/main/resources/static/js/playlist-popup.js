document.addEventListener("click", (event) => {
    const openButton = event.target.closest(
        "[data-playlist-popup-open]"
    );

    if(openButton) {
        openPlaylistPopup(
            openButton.dataset.postId
        );

        return;
    }

    const closeButton = event.target.closest(
        "[data-playlist-popup-close]"
    );

    if(closeButton) {
        closeButton.closest("dialog")?.close();
    }
});

function openPlaylistPopup(postId) {
    const dialog = document.getElementById(
        "playlist-add-dialog"
    );

    const postIdInput = dialog?.querySelector(
        "[data-selected-post-id]"
    );

    if(!dialog || !postIdInput || !postId) {
        return;
    }

    postIdInput.value = postId;

    if(!dialog.open) {
        dialog.showModal();
    }
}

const playlistDialog = document.getElementById("playlist-add-dialog");

playlistDialog?.addEventListener("click", (event) => {
    if(event.target == playlistDialog) {
        playlistDialog.close();
    }
})