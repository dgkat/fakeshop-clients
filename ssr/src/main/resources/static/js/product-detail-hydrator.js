class ProductDetailIslandHydrator {
    async init() {
        console.log('[Hydrator] Initializing...');

        try {
            await this.waitForIslands();

            if (window.islandSearch?.setupIslandModule) {
                console.log('[Hydrator] Calling setupIslandModule...');
                window.islandSearch.setupIslandModule();
            } else {
                console.error('[Hydrator] setupIslandModule not found');
            }
        } catch (error) {
            console.error('[Hydrator] Failed:', error);
        }
    }

    async waitForIslands(timeout = 5000) {
        const startTime = Date.now();

        while (!window.islandSearch) {
            if (Date.now() - startTime > timeout) {
                throw new Error('Timeout waiting for island script');
            }
            await new Promise(resolve => setTimeout(resolve, 100));
        }

        console.log('[Hydrator] Island script loaded');
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new ProductDetailIslandHydrator().init();
    });
} else {
    new ProductDetailIslandHydrator().init();
}