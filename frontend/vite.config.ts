import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8790/challenges/svc',
        changeOrigin: true,
        // The backend context path is already included in `target`; remove the
        // frontend-only `/api` prefix before forwarding to Spring MVC.
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
