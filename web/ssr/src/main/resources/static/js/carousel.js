/**
 * Carousel Controller for Product Detail Page
 * Adds arrow navigation (desktop) and syncs dot indicators with scroll position.
 */
class CarouselController {
    constructor() {
        this.track = document.querySelector('.carousel-track');
        this.dots = document.querySelectorAll('.carousel-dot');
        this.prevBtn = document.querySelector('.carousel-arrow-prev');
        this.nextBtn = document.querySelector('.carousel-arrow-next');

        if (!this.track) return;

        this.init();
    }

    init() {
        // Arrow click handlers
        if (this.prevBtn) {
            this.prevBtn.addEventListener('click', () => this.scroll(-1));
        }
        if (this.nextBtn) {
            this.nextBtn.addEventListener('click', () => this.scroll(1));
        }

        // Sync dots and arrows on scroll
        this.track.addEventListener('scroll', () => {
            if (!this.ticking) {
                requestAnimationFrame(() => this.updateState());
                this.ticking = true;
            }
        });

        // Set initial arrow visibility
        this.updateArrows();
    }

    scroll(direction) {
        const slideWidth = this.track.clientWidth;
        this.track.scrollBy({ left: direction * slideWidth, behavior: 'smooth' });
    }

    updateState() {
        this.updateDots();
        this.updateArrows();
        this.ticking = false;
    }

    updateDots() {
        if (this.dots.length === 0) return;
        const slideWidth = this.track.clientWidth;
        const index = Math.round(this.track.scrollLeft / slideWidth);

        this.dots.forEach((dot, i) => {
            dot.classList.toggle('active', i === index);
        });
    }

    updateArrows() {
        const atStart = this.track.scrollLeft <= 0;
        const atEnd = this.track.scrollLeft + this.track.clientWidth >= this.track.scrollWidth - 1;

        if (this.prevBtn) this.prevBtn.classList.toggle('hidden', atStart);
        if (this.nextBtn) this.nextBtn.classList.toggle('hidden', atEnd);
    }
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => new CarouselController());
} else {
    new CarouselController();
}
