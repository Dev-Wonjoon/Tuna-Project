document.addEventListener("DOMContentLoaded", () => {
    const createDialog = document.getElementById(
        "playlist-create-dialog"
    );

    if(!createDialog) {
        return;
    }

    if(createDialog.dataset.playlistCreateAutoOpen === 'true') {
        createDialog.showModal();

        createDialog.querySelector("input[name='name']")?.focus();
    }

    createDialog.addEventListener("click", event => {
        if(event.target === createDialog) {
            createDialog.close();
        }
    });
});

document.addEventListener("click", event => {
    const target = event.target;

    if(!(target instanceof Element)) {
        return;
    }

    const openButton = target.closest(
        '[data-playlist-create-open]'
    );

    if(openButton) {
        openPlaylistCreatePopup(openButton);
        return;
    }

    const closeButton = target.closest(
        '[data-playlist-create-close]'
    );

    if(closeButton) {
        closeButton.closest("dialog")?.close();
    }
});

function openPlaylistCreatePopup(openButton) {
    const createDialog = document.getElementById(
        "playlist-create-dialog"
    );

    if(!createDialog) {
        return;
    }

    const addDialog = createDialog.querySelector(
        '#playlist-add-dialog'
    );

    const selectedPostId = addDialog
        ?.querySelector('[data-selected-post-id]')
        ?.value;

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