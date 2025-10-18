class ProductDetailIslandHydrator {
    async init() {
        console.log('[Hydrator] Initializing...');

        try {
            // Wait for the island script to load
            await this.waitForIslands();

            // Call the setup function to initialize
            if (window.islandSearch?.org?.example?.fakeshop_clients?.island?.setupIslandModule) {
                console.log('[Hydrator] Calling setupIslandModule...');
                window.islandSearch.org.example.fakeshop_clients.island.setupIslandModule();
            } else {
                console.error('[Hydrator] setupIslandModule not found');
                return;
            }

            // Now render the island
            if (window.renderSearchIsland) {
                const container = document.getElementById('search-island-root');
                if (container) {
                    const island = window.renderSearchIsland();
                    container.appendChild(island);
                    console.log('[Hydrator] ✅ Search island rendered');
                }
            }
        } catch (error) {
            console.error('[Hydrator] Failed:', error);
        }
    }

    async waitForIslands(timeout = 5000) {
        const startTime = Date.now();

        while (!window.islandSearch) {
            if (Date.now() - startTime > timeout) {
                console.error('[Hydrator] Timeout waiting for islandSearch module');
                throw new Error('Timeout waiting for island script to load');
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