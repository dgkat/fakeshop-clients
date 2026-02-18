config.plugins = (config.plugins || []).concat([
    new (require('webpack')).DefinePlugin({
        '__BACKEND_BASE_URL__': JSON.stringify(
            process.env.BACKEND_BASE_URL || 'http://localhost:8080'
        )
    })
]);
