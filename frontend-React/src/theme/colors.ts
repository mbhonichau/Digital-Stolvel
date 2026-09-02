export const themeColors = {
  brand: {
    yellow: '#FFCC00',
    yellowHover: '#E6B800',
    yellowLight: '#FFF8E1',
    dark: '#121212',
    charcoal: '#1E1E1E',
    surface: '#292929',
    blue: '#004F71',
  },
  status: {
    success: '#10B981',
    warning: '#F59E0B',
    error: '#EF4444',
    info: '#3B82F6',
  },
  neutral: {
    bg: '#F9FAFB',
    surface: '#FFFFFF',
    textPrimary: '#111827',
    textSecondary: '#6B7280',
    border: '#E5E7EB',
  },
} as const;

export type ThemeColors = typeof themeColors;
