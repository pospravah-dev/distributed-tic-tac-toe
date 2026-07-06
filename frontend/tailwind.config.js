/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'tictactoe-blue': '#3B82F6',
        'tictactoe-red': '#EF4444',
        'tictactoe-purple': '#8B5CF6',
        'tictactoe-green': '#10B981',
      },
    },
  },
  plugins: [],
};
