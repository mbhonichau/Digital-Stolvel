import type { Config } from 'tailwindcss';
import { tokens } from './src/theme/tokens';

const config: Config = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // MTN MoMo brand tokens: MoMo Blue, MTN Sunshine and white.
        'mtn-base': tokens.colors.base.dark, // #004F71
        'mtn-surface': tokens.colors.base.surface, // #00445F
        'mtn-card': tokens.colors.base.card, // #003B55
        'mtn-border': tokens.colors.base.border, // #2E718D
        'mtn-gold': {
          DEFAULT: tokens.colors.primary.DEFAULT, // #FFCB05
          hover: tokens.colors.primary.hover, // #E6B705
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
          DEFAULT: tokens.colors.tertiary.DEFAULT, // #004F71
          hover: tokens.colors.tertiary.hover,
          muted: tokens.colors.tertiary.muted,
        },
        'mtn-cream': {
          DEFAULT: tokens.colors.text.DEFAULT, // #FFFFFF
          secondary: tokens.colors.text.secondary,
          muted: tokens.colors.text.muted,
        },

        // Direct MoMo aliases
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
