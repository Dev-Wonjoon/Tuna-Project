document.addEventListener("DOMContentLoaded", () => {
    const createDialog = document.getElementById(
        "playlist-create-dialog"
    );

    if(createDialog?.dataset.playlistCreateAutoOpen === 'true') {
        createDialog.showModal();

        createDialog.querySelector("input[name='name']")?.focus();
    }

});

document.addEventListener('click', (event) => {
    const target = event.target;

    if(!(target instanceof Element)) {
        return;
    }

    const playlistOpenButton = target.closest(
        '[data-playlist-popup-open]'
    );

    if(playlistOpenButton) {
        openPlaylistPopup(
            playlistOpenButton.dataset.postId
        );

        return;
    }

    const createOpenButton = target.closest(
        '[data-playlist-create-open]'
    );

    if(createOpenButton) {
        openPlaylistCreatePopup(createOpenButton);
        return;
    }

    const playlistCloseButton = target.closest(
        '[data-playlist-popup-close]'
    );

    if(playlistCloseButton) {
        document.getElementById('playlist-add-dialog')?.close();
    }
});

function openPlaylistPopup(postId) {
    const dialog = document.getElementById(
        'playlist-add-dialog'
    );

    const postIdInput = dialog?.querySelector(
        '[data-selected-post-id]'
    );

    if(!dialog || !postIdInput || !postId) {
        return;
    }

    postIdInput.value = postId;

    if(!dialog.open) {
        dialog.showModal();
    }
}

function openPlaylistCreatePopup(openButton) {
    const createDialog = document.getElementById(
        'playlist-create-dialog'
    );

    if(!createDialog) {
        return;
    }

    const addDialog = openButton.closest(
        '#playlist-add-dialog'
    );

    const selectedPostId = addDialog?.querySelector('[data-selected-post-id]')?.value;

    const postIdInput = createDialog.querySelector(
        '[data-playlist-create-post-id]'
    );

    const returnUrlInput = createDialog.querySelector(
        '[data-playlist-create-return-url]'
    );

    if(postIdInput) {
        postIdInput.value = selectedPostId ?? "";
        postIdInput.disabled = !selectedPostId;
    }

    if(returnUrlInput) {
        returnUrlInput.value =
            window.location.pathname
            + window.location.search;
    }

    addDialog?.close();

    if(!createDialog.open) {
        createDialog.showModal();
    }

    createDialog.querySelector("input[name='name']")?.focus();
}