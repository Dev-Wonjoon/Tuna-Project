document.addEventListener("DOMContentLoaded", () => {
    const toasts = [
        document.getElementById("loginToast"),
        document.getElementById("logoutToast"),
        document.getElementById("deleteToast")
    ];

    toasts.forEach(toast => {
        if (toast) {
            setTimeout(() => {
                toast.remove();
            }, 3000);
        }
    });
});