/**
 * MTN MoMo Design System Tokens.
 * The core palette follows the supplied brand swatches: MoMo Blue (#004F71),
 * MTN Sunshine (#FFCB05), and white (#FFFFFF).
 */

export const tokens = {
  colors: {
    // MoMo Blue surfaces. Darker shades preserve readable white type while
    // keeping the product unmistakably within the supplied blue family.
    base: {
      dark: '#004F71',
      surface: '#00445F',
      card: '#003B55',
      border: '#2E718D',
    },
    // MTN Sunshine
    primary: {
      DEFAULT: '#FFCB05',
      hover: '#E6B705',
      muted: '#FFCB0526',
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
    // MoMo Blue
    tertiary: {
      DEFAULT: '#004F71',
      hover: '#003B55',
      muted: '#004F7126',
    },
    // White brand type
    text: {
      DEFAULT: '#FFFFFF',
      secondary: '#D8E8EE',
      muted: '#9FC2D0',
    },
  },
} as const;

export type ThemeTokens = typeof tokens;
export default tokens;
