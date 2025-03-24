import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  define: {
    global: {}, // Определяем пустой объект для `global`
  },
  plugins: [vue()],
  build: {
    outDir: '../src/main/resources/static', // Директория для сборки Vite
    emptyOutDir: true, // Удалить содержимое перед сборкой
  },
  server: {
    proxy: {
      '/ws': {
        target: 'http://localhost:8080',
        ws: true, // Включаем поддержку WebSocket
        changeOrigin: true,
      },
    },
  },
})
