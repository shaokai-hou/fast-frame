import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
      "@vue-office/pdf": path.resolve(
        __dirname,
        "node_modules/@vue-office/pdf/lib/v3/vue-office-pdf.mjs",
      ),
      "@vue-office/docx": path.resolve(
        __dirname,
        "node_modules/@vue-office/docx/lib/v3/vue-office-docx.mjs",
      ),
      "@vue-office/excel": path.resolve(
        __dirname,
        "node_modules/@vue-office/excel/lib/v3/vue-office-excel.mjs",
      ),
      "@vue-office/pptx": path.resolve(
        __dirname,
        "node_modules/@vue-office/pptx/lib/v3/vue-office-pptx.mjs",
      ),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: "modern-compiler",
      },
    },
  },
  server: {
    host: "0.0.0.0",
    port: 3000,
    proxy: {
      "/api": {
        target: "http://localhost:8100",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes("element-plus")) {
            return "element-plus";
          }
          if (
            id.includes("vue") ||
            id.includes("pinia") ||
            id.includes("vue-router")
          ) {
            return "vue-vendor";
          }
          if (id.includes("xlsx")) {
            return "xlsx";
          }
        },
      },
    },
  },
});
