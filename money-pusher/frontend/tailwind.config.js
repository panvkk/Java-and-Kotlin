/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        asphalt: { 950: '#0b0c0d', 900: '#121416', 800: '#1b1e21', 700: '#292d31', 600: '#3e4449' },
        road: { 50: '#f5f4ef', 100: '#e8e6de', 300: '#b8b7ae' },
        signal: { orange: '#f3a43b', green: '#c4d34c', red: '#e27364' },
      },
      fontFamily: { sans: ['Inter', 'ui-sans-serif', 'system-ui'], display: ['Space Grotesk', 'ui-sans-serif'] },
      boxShadow: { asphalt: '0 20px 60px rgba(0,0,0,.32)', press: '0 5px 0 #b57a23' },
    },
  },
  plugins: [],
};
