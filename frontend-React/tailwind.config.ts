import type { Config } from 'tailwindcss';
import { tokens } from './src/theme/tokens';

const config: Config = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // Mzansi Futurism Theme Tokens
        'mtn-base': tokens.colors.base.dark, // #0B0B0C
        'mtn-surface': tokens.colors.base.surface, // #131316
        'mtn-card': tokens.colors.base.card, // #1A1A1F
        'mtn-border': tokens.colors.base.border, // #292933
        'mtn-gold': {
          DEFAULT: tokens.colors.primary.DEFAULT, // #FFB612
          hover: tokens.colors.primary.hover, // #E5A20C
          muted: tokens.colors.primary.muted,
        },
        'mtn-green': {
          DEFAULT: tokens.colors.success.DEFAULT, // #007A4D
          hover: tokens.colors.success.hover,
          muted: tokens.colors.success.muted,
        },
        'mtn-red': {
          DEFAULT: tokens.colors.failure.DEFAULT, // #DE3831
          hover: tokens.colors.failure.hover,
          muted: tokens.colors.failure.muted,
        },
        'mtn-blue': {
          DEFAULT: tokens.colors.tertiary.DEFAULT, // #002395
          hover: tokens.colors.tertiary.hover,
          muted: tokens.colors.tertiary.muted,
        },
        'mtn-cream': {
          DEFAULT: tokens.colors.text.DEFAULT, // #F5F1E8
          secondary: tokens.colors.text.secondary,
          muted: tokens.colors.text.muted,
        },

        // Direct Mzansi Aliases
        mzansi: {
          base: tokens.colors.base.dark,
          surface: tokens.colors.base.surface,
          card: tokens.colors.base.card,
          border: tokens.colors.base.border,
          gold: tokens.colors.primary.DEFAULT,
          green: tokens.colors.success.DEFAULT,
          red: tokens.colors.failure.DEFAULT,
          blue: tokens.colors.tertiary.DEFAULT,
          cream: tokens.colors.text.DEFAULT,
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'Roboto',
          'sans-serif',
        ],
      },
    },
  },
  plugins: [],
};

export default config;
