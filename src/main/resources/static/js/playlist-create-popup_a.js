document.addEventListener('DOMContentLoaded', () => {
    const createDialog = document.getElementById(
        'playlist-create-dialog'
    );

    if(!createDialog) { return; }

    if(createDialog.dataset.playlistCreateAutoOpen === 'true') {
        setReturnedUrl(createDialog);
        showPlaylistCreatePopup(createDialog);
    }

    createDialog.addEventListener('click', (event) => {
        if(event.target === createDialog) {
            createDialog.close();
        }
    });
});

document.addEventListener('click', (event) => {
    const target = event.target;

    if(!(target instanceof Element)) { return; }

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
        document.getElementById('playlist-create-dialog')?.close();
    }
});

function openPlaylistCreatePopup(openButton) {
    const createDialog = document.getElementById("playlist-create-dialog");

    if(!createDialog) { return; }

    const addDialog = openButton.closest('#playlist-add-dialog');

    const selectedPostId = addDialog
        ?.querySelectorAll('[data-selected-post-id]')
        ?.value;

    setPostId(createDialog, selectedPostId);
    setReturnedUrl(createDialog);

    addDialog?.close();

    showPlaylistCreatePopup(createDialog);
}

function setPostId(createDialog, postId) {
    const postIdInput = createDialog.querySelector(
        '[data-playlist-create-post-id]'
    );

    if(!postIdInput) {
        return;
    }

    postIdInput.value = postId ?? "";
    postIdInput.disabled = !postId;
}

function setReturnedUrl(createDialog) {
    const returnUrlInput = createDialog.querySelector(
        '[data-playlist-create-return-url]'
    );

    if(!returnUrlInput) {
        return;
    }

    returnUrlInput.value =
        window.location.pathname
        + window.location.search;
}

function showPlaylistCreatePopup(createDialog) {
    if(!createDialog.open) {
        createDialog.showModal();
    }

    createDialog.querySelector("input[name='name']")?.focus();
}