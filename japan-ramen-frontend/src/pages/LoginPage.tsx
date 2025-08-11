import React, { useState, useEffect } from 'react';
import { useNavigate, Link as RouterLink, useLocation } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  TextField, 
  Button, 
  Card, 
  CardContent, 
  Alert, 
  InputAdornment, 
  IconButton,
  CircularProgress
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import ReCAPTCHA from 'react-google-recaptcha';
import { useAuth } from '../contexts/AuthContext';
import { getRecaptchaSiteKey } from '../services/api';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import LoginIcon from '@mui/icons-material/Login';

const LoginPage: React.FC = () => {
  const { t } = useTranslation();
  const { login, isLoading, error } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  
  // Get the redirect path from location state or default to homepage
  const from = location.state?.from?.pathname || '/';
  
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [recaptchaResponse, setRecaptchaResponse] = useState<string | null>(null);
  const [recaptchaSiteKey, setRecaptchaSiteKey] = useState<string | null>(null);
  const [loadingRecaptcha, setLoadingRecaptcha] = useState(true);

  useEffect(() => {
    const fetchRecaptchaSiteKey = async () => {
      try {
        setLoadingRecaptcha(true);
        const siteKey = await getRecaptchaSiteKey();
        setRecaptchaSiteKey(siteKey);
      } catch (error) {
        console.error('Error fetching reCAPTCHA site key:', error);
        setFormError(t('error.recaptchaKey'));
        // Fallback to test key in case of error
        setRecaptchaSiteKey('6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI');
      } finally {
        setLoadingRecaptcha(false);
      }
    };

    fetchRecaptchaSiteKey();
  }, [t]);

  const handleTogglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleRecaptchaChange = (value: string | null) => {
    setRecaptchaResponse(value);
    if (!value) {
      setFormError(t('auth.recaptchaRequired'));
    } else {
      setFormError(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Basic validation
    if (!username.trim()) {
      setFormError(t('auth.usernameRequired'));
      return;
    }
    
    if (!password) {
      setFormError(t('auth.passwordRequired'));
      return;
    }

    if (!recaptchaResponse) {
      setFormError(t('auth.recaptchaRequired'));
      return;
    }
    
    setFormError(null);
    
    try {
      await login({ username, password, recaptchaResponse });
      // Redirect to the page user came from or home page
      navigate(from, { replace: true });
    } catch (err) {
      console.error('Login error:', err);
    }
  };

  return (
    <Box sx={{ 
      display: 'flex', 
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '70vh'
    }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        style={{ width: '100%', maxWidth: 450 }}
      >
        <Card sx={{ boxShadow: 3 }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h4" component="h1" textAlign="center" fontWeight="bold" gutterBottom>
              {t('auth.login')}
            </Typography>
            
            {(error || formError) && (
              <Alert severity="error" sx={{ my: 2 }}>
                {formError || error}
              </Alert>
            )}
            
            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
              <TextField
                label={t('auth.username')}
                variant="outlined"
                fullWidth
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                margin="normal"
                required
                autoFocus
              />
              
              <TextField
                label={t('auth.password')}
                variant="outlined"
                fullWidth
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                margin="normal"
                required
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleTogglePasswordVisibility}
                        edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <Box sx={{ mt: 3, mb: 3, display: 'flex', justifyContent: 'center' }}>
                {loadingRecaptcha ? (
                  <CircularProgress size={40} />
                ) : recaptchaSiteKey ? (
                  <ReCAPTCHA
                    sitekey={recaptchaSiteKey}
                    onChange={handleRecaptchaChange}
                    theme="dark"
                  />
                ) : (
                  <Alert severity="error">{t('error.recaptchaKey')}</Alert>
                )}
              </Box>
              
              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                size="large"
                disabled={isLoading || !recaptchaResponse || loadingRecaptcha}
                sx={{ mt: 3, mb: 2 }}
                startIcon={<LoginIcon />}
              >
                {isLoading ? t('auth.loggingIn') : t('auth.loginButton')}
              </Button>
              
              <Box sx={{ textAlign: 'center', mt: 2 }}>
                <Typography variant="body2">
                  {t('auth.noAccount')}{' '}
                  <RouterLink to="/register" style={{ color: 'inherit' }}>
                    {t('auth.registerHere')}
                  </RouterLink>
                </Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
};

export default LoginPage; 