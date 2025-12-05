// View Transitions API support for smooth page navigation
(function() {
    // Check if View Transitions API is supported
    if (!document.startViewTransition) {
        return; // Graceful degradation - normal navigation
    }

    // Intercept navigation clicks
    document.addEventListener('click', (e) => {
        const link = e.target.closest('a[data-transition-link]');

        if (!link) return;

        const href = link.getAttribute('href');

        // Only handle internal links
        if (!href || href.startsWith('http') || href.startsWith('//')) {
            return;
        }

        // Don't handle if it's a modifier click (ctrl, shift, etc.)
        if (e.ctrlKey || e.shiftKey || e.metaKey || e.altKey) {
            return;
        }

        e.preventDefault();

        // Start view transition
        document.startViewTransition(() => {
            return new Promise((resolve) => {
                window.location.href = href;
                // Note: The promise resolves when the new page loads
                // but since we're navigating, this won't actually execute
                // The browser handles it internally
            });
        });
    });
})();