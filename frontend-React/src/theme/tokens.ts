/**
 * Mzansi Futurism Design System Tokens
 * Centralized color palette and visual tokens for MTN MoMo Stokvel.
 */

export const tokens = {
  colors: {
    // Base Canvas & Surface
    base: {
      dark: '#0B0B0C',
      surface: '#131316',
      card: '#1A1A1F',
      border: '#292933',
    },
    // Primary Brand Gold
    primary: {
      DEFAULT: '#FFB612',
      hover: '#E5A20C',
      muted: '#FFB61220',
    },
    // Status Success Green
    success: {
      DEFAULT: '#007A4D',
      hover: '#00643F',
      muted: '#007A4D20',
    },
    // Status Failure Red
    failure: {
      DEFAULT: '#DE3831',
      hover: '#C52E28',
      muted: '#DE383120',
    },
    // Tertiary Deep Blue
    tertiary: {
      DEFAULT: '#002395',
      hover: '#001D7C',
      muted: '#00239520',
    },
    // Primary Typography Cream
    text: {
      DEFAULT: '#F5F1E8',
      secondary: '#AFA99E',
      muted: '#6F6B64',
    },
  },
} as const;

export type ThemeTokens = typeof tokens;
export default tokens;
