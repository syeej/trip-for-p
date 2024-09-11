const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        proxy: {
            '/api/v1': {
                target: 'https://kdt-api-function.azurewebsites.net',
                changeOrigin: true,
                pathRewrite: { '^/api/v1': '' },
            },
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        }
    }
})
