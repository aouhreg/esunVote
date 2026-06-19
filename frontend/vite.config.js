// Vite 設定檔
// Vite 是 Vue 3 的開發伺服器與建置工具

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  // 使用 Vue 3 外掛
  plugins: [vue()],

  server: {
    port: 5173,  // 開發伺服器埠號

    // proxy 設定：解決開發時的前後端跨域問題
    proxy: {
      // 當瀏覽器請求 /api/xxx 時，Vite 會自動轉發到 http://localhost:8080
      '/api': {
        target: 'http://localhost:8080',  // Spring Boot 後端位址
        changeOrigin: true                // 改變請求的 origin 以符合後端
      }
    }
  }
})
