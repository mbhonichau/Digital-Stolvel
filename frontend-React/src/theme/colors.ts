export const themeColors = {
  brand: {
    yellow: '#FFCB05',
    yellowHover: '#E6B705',
    yellowLight: '#FFF5C2',
    dark: '#003B55',
    charcoal: '#00445F',
    surface: '#004F71',
    blue: '#004F71',
  },
  status: {
    success: '#10B981',
    warning: '#F59E0B',
    error: '#EF4444',
    info: '#3B82F6',
  },
  neutral: {
    bg: '#004F71',
    surface: '#FFFFFF',
    textPrimary: '#003B55',
    textSecondary: '#D8E8EE',
    border: '#2E718D',
  },
} as const;

export type ThemeColors = typeof themeColors;
