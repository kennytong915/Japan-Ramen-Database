import React, { useState, useEffect } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
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
  LinearProgress,
  Tooltip,
  CircularProgress
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import ReCAPTCHA from 'react-google-recaptcha';
import { useAuth } from '../contexts/AuthContext';
import { getRecaptchaSiteKey } from '../services/api';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import InfoIcon from '@mui/icons-material/Info';
import HowToRegIcon from '@mui/icons-material/HowToReg';

const RegisterPage: React.FC = () => {
  const { t } = useTranslation();
  const { register, isLoading, error } = useAuth();
  const navigate = useNavigate();
  
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [passwordStrength, setPasswordStrength] = useState(0);
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

  const handleToggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const checkPasswordStrength = (password: string) => {
    let strength = 0;
    
    if (password.length >= 8) strength += 25;
    if (/[A-Z]/.test(password)) strength += 25;
    if (/[a-z]/.test(password)) strength += 25;
    if (/[0-9]/.test(password)) strength += 12.5;
    if (/[^A-Za-z0-9]/.test(password)) strength += 12.5;
    
    return strength;
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newPassword = e.target.value;
    setPassword(newPassword);
    setPasswordStrength(checkPasswordStrength(newPassword));
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
    if (!username.trim() || username.length < 3 || username.length > 30) {
      setFormError(t('auth.usernameRequirements'));
      return;
    }
    
    if (passwordStrength < 75) {
      setFormError(t('auth.passwordRequirements'));
      return;
    }
    
    if (password !== confirmPassword) {
      setFormError(t('auth.passwordsMustMatch'));
      return;
    }

    if (!recaptchaResponse) {
      setFormError(t('auth.recaptchaRequired'));
      return;
    }
    
    setFormError(null);
    
    try {
      await register({ 
        username, 
        password,
        recaptchaResponse: recaptchaResponse
      });
      navigate('/');
    } catch (err) {
      console.error('Registration error:', err);
    }
  };

  // Function to get color based on password strength
  const getPasswordStrengthColor = () => {
    if (passwordStrength < 25) return 'error.main';
    if (passwordStrength < 50) return 'error.light';
    if (passwordStrength < 75) return 'warning.main';
    return 'success.main';
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
              {t('auth.register')}
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
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <Tooltip title={t('auth.usernameRequirements')}>
                        <InfoIcon fontSize="small" color="action" />
                      </Tooltip>
                    </InputAdornment>
                  ),
                }}
              />
              
              <TextField
                label={t('auth.password')}
                variant="outlined"
                fullWidth
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={handlePasswordChange}
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
              
              {password && (
                <Box sx={{ mb: 2, mt: 0.5 }}>
                  <LinearProgress 
                    variant="determinate" 
                    value={passwordStrength} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      bgcolor: 'background.paper',
                      '& .MuiLinearProgress-bar': {
                        bgcolor: getPasswordStrengthColor(),
                      }
                    }} 
                  />
                  <Typography variant="caption" sx={{ color: 'text.secondary', mt: 0.5, display: 'block' }}>
                    {t('auth.passwordRequirements')}
                  </Typography>
                </Box>
              )}
              
              <TextField
                label={t('auth.confirmPassword')}
                variant="outlined"
                fullWidth
                type={showConfirmPassword ? 'text' : 'password'}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                margin="normal"
                required
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle confirm password visibility"
                        onClick={handleToggleConfirmPasswordVisibility}
                        edge="end"
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
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
                startIcon={<HowToRegIcon />}
              >
                {isLoading ? t('auth.registering') : t('auth.registerButton')}
              </Button>
              
              <Box sx={{ textAlign: 'center', mt: 2 }}>
                <Typography variant="body2">
                  {t('auth.hasAccount')}{' '}
                  <RouterLink to="/login" style={{ color: 'inherit' }}>
                    {t('auth.loginHere')}
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

export default RegisterPage; 