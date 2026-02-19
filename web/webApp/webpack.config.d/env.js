config.plugins = (config.plugins || []).concat([
    new (require('webpack')).DefinePlugin({
        '__BACKEND_BASE_URL__': JSON.stringify('https://api.dgkat.com')
    })
]);