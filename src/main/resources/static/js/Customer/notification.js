document.addEventListener('DOMContentLoaded', function() {
    const toasts = document.querySelectorAll('.toast');

    toasts.forEach(toast => {
        // Set a timeout to remove the toast after 5000ms (5 seconds)
        setTimeout(() => {
            toast.style.animation = 'fadeOut 0.5s ease-in forwards';
            // Wait for the fadeOut animation to finish before removing from DOM
            setTimeout(() => {
                toast.remove();
            }, 500);
        }, 5000);
    });
});