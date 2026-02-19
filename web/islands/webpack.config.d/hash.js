if (config.mode === 'production') {
    const base = config.output.filename.replace('.js', '');
    config.output.filename = `${base}.[contenthash:8].js`;
}
