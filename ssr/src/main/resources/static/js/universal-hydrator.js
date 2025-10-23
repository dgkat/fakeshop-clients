class UniversalHydrator {
    async init() {
        console.log('[UniversalHydrator] Initializing...');

        try {
            // Wait for all island scripts to load
            await this.waitForIslands();

            // Initialize search island if present
            if (document.getElementById('search-island-root')) {
                console.log('[UniversalHydrator] Initializing search island...');
                if (window.islandSearch?.setupIslandModule) {
                    window.islandSearch.setupIslandModule();
                }
            }

            // Initialize product list island if present
            if (document.getElementById('product-list-island-root')) {
                console.log('[UniversalHydrator] Initializing product list island...');
                if (window.islandProductList?.setupProductListIslandModule) {
                    window.islandProductList.setupProductListIslandModule();
                }
            }

            console.log('[UniversalHydrator] ✅ All islands hydrated successfully');
        } catch (error) {
            console.error('[UniversalHydrator] Failed:', error);
        }
    }

    async waitForIslands(timeout = 5000) {
        const startTime = Date.now();

        // Wait for at least one island to load
        while (!window.islandSearch && !window.islandProductList) {
            if (Date.now() - startTime > timeout) {
                throw new Error('Timeout waiting for island scripts');
            }
            await new Promise(resolve => setTimeout(resolve, 100));
        }

        console.log('[UniversalHydrator] Island scripts loaded');
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new UniversalHydrator().init();
    });
} else {
    new UniversalHydrator().init();
}