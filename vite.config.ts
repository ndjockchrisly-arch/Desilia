import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // Charge les variables d'environnement de manière sécurisée sans bloquer le build
  const env = loadEnv(mode, process.cwd(), '');
  const geminiApiKey = env.GEMINI_API_KEY || env.VITE_GEMINI_API_KEY || '';

  return {
    plugins: [react()],
    base: '/',
    build: {
      outDir: 'dist',
      sourcemap: false,
    },
    define: {
      // Injection sûre des variables sans plantage au build si absentes
      'process.env.GEMINI_API_KEY': JSON.stringify(geminiApiKey),
      'process.env.API_KEY': JSON.stringify(geminiApiKey),
    },
  };
});
