class UniversalHydrator {
    async init() {
        console.log('[UniversalHydrator] Initializing...');

        try {
            await this.waitForIslandsBundle();

            // Initialize search island if present
            if (document.getElementById('search-island-root')) {
                console.log('[UniversalHydrator] Initializing search island...');
                if (window.islands?.setupSearchIsland) {
                    window.islands.setupSearchIsland();
                } else {
                    console.error('[UniversalHydrator] setupSearchIsland not found');
                }
            }

            // Initialize product list island if present
            if (document.getElementById('product-list-island-root')) {
                console.log('[UniversalHydrator] Initializing product list island...');
                if (window.islands?.setupProductListIsland) {
                    window.islands.setupProductListIsland();
                } else {
                    console.error('[UniversalHydrator] setupProductListIsland not found');
                }
            }

            console.log('[UniversalHydrator] ✅ All islands hydrated successfully');
        } catch (error) {
            console.error('[UniversalHydrator] Failed:', error);
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

        console.log('[UniversalHydrator] Islands bundle loaded');
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new UniversalHydrator().init();
    });
} else {
    new UniversalHydrator().init();
}