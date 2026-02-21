class UniversalHydrator {
    async init() {
        await this.waitForIslandsBundle();

        if (document.getElementById('search-island-root')) {
            window.islands?.setupSearchIsland?.();
        }

        if (document.getElementById('product-list-island-root')) {
            window.islands?.setupProductListIsland?.();
        }
    }

    async waitForIslandsBundle(timeout = 5000) {
        const startTime = Date.now();

        while (!window.islands) {
            if (Date.now() - startTime > timeout) {
                throw new Error('Timeout waiting for islands bundle');
            }
            await new Promise(resolve => setTimeout(resolve, 100));
        }
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new UniversalHydrator().init();
    });
} else {
    new UniversalHydrator().init();
}