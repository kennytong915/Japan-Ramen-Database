import React, { createContext, useContext, useState, useEffect } from 'react';
import { Theme } from '@mui/material/styles';
import lightTheme from '../themes/lightTheme';
import darkTheme from '../themes/darkTheme';

type ThemeMode = 'light' | 'dark';

interface ThemeContextProps {
  mode: ThemeMode;
  theme: Theme;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextProps | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Check localStorage for saved theme preference, default to 'dark'
  const savedTheme = localStorage.getItem('themeMode') as ThemeMode | null;
  const [mode, setMode] = useState<ThemeMode>(savedTheme || 'dark');
  
  // Set the theme based on the current mode
  const theme = mode === 'light' ? lightTheme : darkTheme;
  
  // Toggle between light and dark themes
  const toggleTheme = () => {
    const newMode = mode === 'light' ? 'dark' : 'light';
    setMode(newMode);
    localStorage.setItem('themeMode', newMode);
  };
  
  // Initialize theme from localStorage on mount and force dark mode if not set
  useEffect(() => {
    const storedTheme = localStorage.getItem('themeMode') as ThemeMode | null;
    if (storedTheme) {
      setMode(storedTheme);
    } else {
      // Set dark mode as default if not explicitly set
      localStorage.setItem('themeMode', 'dark');
    }
  }, []);
  
  return (
    <ThemeContext.Provider value={{ mode, theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = (): ThemeContextProps => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}; 