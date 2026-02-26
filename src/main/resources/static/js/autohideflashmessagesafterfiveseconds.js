// Auto-hide flash messages after 5 seconds
window.addEventListener('DOMContentLoaded', (event) => {
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(alert => {
        setTimeout(() => {
// Use Bootstrap's fade class for smooth disappearance
            alert.classList.remove('show'); // triggers fade-out
            setTimeout(() => alert.remove(), 500); // remove from DOM after fade
        }, 5000); // 5000ms = 5 seconds
    });
});