document.addEventListener("DOMContentLoaded", () => {
    const page = document.querySelector(
        '[data-playlist-edit]'
    );

    if(!page) {
        return;
    }

    const selectAll = page.querySelector(
        '[data-playlist-select-all]'
    );

    const itemCheckboxes = Array.from(
        page.querySelectorAll('[data-playlist-item]')
    );

    const selectedDeleteButton = page.querySelector(
        '[data-playlist-selected-delete]'
    );

    const selectedCount = page.querySelector(
        '[data-playlist-selected-count]'
    );

    function updateSelectionState() {
        const checkedCount = itemCheckboxes.filter(
            checkbox => checkbox.checked
        ).length;

        if(selectedCount) {
            selectedCount.textContent = String(checkedCount);
        }

        if(selectedDeleteButton) {
            selectedDeleteButton.disabled = checkedCount === 0;
        }

        if(selectAll) {
            selectAll.checked = itemCheckboxes.length > 0
            && checkedCount === itemCheckboxes.length;

            selectAll.indeterminate = checkedCount > 0
            && checkedCount < itemCheckboxes.length;
        }
    }

    selectAll?.addEventListener("change", () => {
        itemCheckboxes.forEach(checkbox => {
            checkbox.checked = selectAll.checked;
        });

        updateSelectionState();
    });

    itemCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", updateSelectionState);
    });

    updateSelectionState();
});