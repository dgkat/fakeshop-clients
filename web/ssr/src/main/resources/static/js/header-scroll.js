/**
 * Header Scroll Controller for SSR Pages
 * Mirrors the logic from useScrollOffset.kt hook
 *
 * Applies scroll-reactive behavior to desktop header:
 * - Hides header on scroll down
 * - Shows header on scroll up
 * - Always visible at top of page
 */
class HeaderScrollController {
    constructor() {
        this.header = document.querySelector('.header');
        this.lastScrollY = 0;
        this.currentOffset = 0;
        this.ticking = false;
        this.threshold = 5; // Same as React hook (5px)
        this.isDesktop = false;

        this.init();
    }

    init() {
        if (!this.header) {
            console.warn('Header element not found');
            return;
        }

        // Check if desktop
        this.checkBreakpoint();

        // Listen for resize
        window.addEventListener('resize', () => this.checkBreakpoint());

        // Listen for scroll (only on desktop)
        if (this.isDesktop) {
            window.addEventListener('scroll', () => this.onScroll());
            this.lastScrollY = window.scrollY;
        }
    }

    checkBreakpoint() {
        const wasDesktop = this.isDesktop;
        this.isDesktop = window.matchMedia('(min-width: 768px)').matches;

        if (wasDesktop !== this.isDesktop) {
            if (this.isDesktop) {
                // Switched to desktop - add scroll listener
                window.addEventListener('scroll', () => this.onScroll());
                this.lastScrollY = window.scrollY;
            } else {
                // Switched to mobile - remove scroll listener and reset
                window.removeEventListener('scroll', () => this.onScroll());
                this.header.style.transform = 'translateY(0px)';
            }
        }
    }

    onScroll() {
        if (!this.ticking) {
            requestAnimationFrame(() => this.updateHeader());
            this.ticking = true;
        }
    }

    updateHeader() {
        if (!this.header || !this.isDesktop) {
            this.ticking = false;
            return;
        }

        const behavior = this.header.getAttribute('data-scroll-behavior');

        // Only apply to scroll-reactive headers
        if (behavior !== 'scroll-reactive') {
            this.ticking = false;
            return;
        }

        const currentScrollY = window.scrollY;
        const delta = currentScrollY - this.lastScrollY;

        // Calculate header height for maxOffset
        const maxOffset = this.header.offsetHeight;

        // Determine target state based on scroll direction and threshold
        // Mirrors useScrollOffset.kt logic exactly
        let newOffset;
        if (currentScrollY <= 0) {
            // At top of page → always visible
            newOffset = 0;
        } else if (delta > this.threshold) {
            // Scrolling down beyond threshold → hide
            newOffset = -maxOffset;
        } else if (delta < -this.threshold) {
            // Scrolling up beyond threshold → show
            newOffset = 0;
        } else {
            // Within threshold → keep current state
            newOffset = this.currentOffset;
        }

        // Only update if state changed
        if (newOffset !== this.currentOffset) {
            this.currentOffset = newOffset;
            this.header.style.transform = `translateY(${newOffset}px)`;
        }

        this.lastScrollY = currentScrollY;
        this.ticking = false;
    }
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new HeaderScrollController();
    });
} else {
    new HeaderScrollController();
}
