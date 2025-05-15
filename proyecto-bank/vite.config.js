import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  base: '/', 
  server: {
    proxy: {
      // Todas las peticiones que comiencen por /api serán redirigidas al back-end
      '/api': {
        target: 'http://localhost:8080', // URL de tu back-end
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
