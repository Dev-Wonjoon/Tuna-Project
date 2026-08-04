const editButtons = document.querySelectorAll('[data-comment-edit-open]');

editButtons.forEach((button) => {
    button.addEventListener('click', () => {
        const comment = button.closest('[data-comment-item]');
        if (!comment) {
            return;
        }

        const view = comment.querySelector('[data-comment-view]');
        const form = comment.querySelector('[data-comment-edit-form]');
        if (!view || !form) {
            return;
        }

        const textarea = form.querySelector('textarea[name="content"]');

        editButtons.forEach((editButton) => {
            editButton.classList.add('hidden');
        });

        view.classList.add('hidden');
        form.classList.remove('hidden');
        textarea?.focus();
    });
});