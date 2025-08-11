import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, LoginRequest, RegistrationRequest } from '../types';
import * as api from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegistrationRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const isAuthenticated = !!token && !!user;

  useEffect(() => {
    const fetchCurrentUser = async () => {
      if (token) {
        setIsLoading(true);
        try {
          const userData = await api.getCurrentUser();
          setUser(userData);
        } catch (error) {
          console.error('Error fetching user data:', error);
          setUser(null);
          setToken(null);
          localStorage.removeItem('token');
          setError('認證已過期，請重新登入');
        } finally {
          setIsLoading(false);
        }
      }
    };

    fetchCurrentUser();
  }, [token]);

  const login = async (credentials: LoginRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const authToken = await api.login(credentials);
      
      // Check if we have a valid token
      if (!authToken) {
        throw new Error('No token received from server');
      }
      
      setToken(authToken);
      // Token is already stored in localStorage by the api.login function
      
      // Make sure to use the token for the getCurrentUser request
      const userData = await api.getCurrentUser();
      setUser(userData);
    } catch (error) {
      console.error('Login error:', error);
      setError('登入失敗，請檢查您的憑證');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (userData: RegistrationRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      await api.register(userData);
      // Auto login after registration
      await login({ username: userData.username, password: userData.password, recaptchaResponse: userData.recaptchaResponse });
    } catch (error) {
      console.error('Registration error:', error);
      setError('註冊失敗，請稍後再試');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated,
        isLoading,
        error,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 