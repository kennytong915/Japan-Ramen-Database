import React, { useState, useEffect } from 'react';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, IconButton, Menu, MenuItem, Box, Avatar, Drawer, List, ListItem, ListItemText, ListItemIcon, Tooltip, Divider } from '@mui/material';
import { useTheme as useMuiTheme } from '@mui/material/styles';
import { motion } from 'framer-motion';
import MenuIcon from '@mui/icons-material/Menu';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import SearchIcon from '@mui/icons-material/Search';
import PersonIcon from '@mui/icons-material/Person';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import HomeIcon from '@mui/icons-material/Home';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';

const Header: React.FC = () => {
  const { t } = useTranslation();
  const muiTheme = useMuiTheme();
  const { mode, toggleTheme } = useTheme();
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  
  const isHomePage = location.pathname === '/';
  
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [userMenuAnchor, setUserMenuAnchor] = useState<null | HTMLElement>(null);
  const [scrolled, setScrolled] = useState(false);
  
  // Track scrolling to change navbar appearance
  useEffect(() => {
    const handleScroll = () => {
      const isScrolled = window.scrollY > 50;
      if (isScrolled !== scrolled) {
        setScrolled(isScrolled);
      }
    };

    if (isHomePage) {
      window.addEventListener('scroll', handleScroll);
      return () => {
        window.removeEventListener('scroll', handleScroll);
      };
    }
  }, [scrolled, isHomePage]);
  
  const handleUserMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setUserMenuAnchor(event.currentTarget);
  };
  
  const handleUserMenuClose = () => {
    setUserMenuAnchor(null);
  };

  const handleLogout = () => {
    logout();
    handleUserMenuClose();
    navigate('/');
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  return (
    <>
      <AppBar 
        position={isHomePage ? "fixed" : "static"} 
        elevation={isHomePage && scrolled ? 4 : 0} 
        sx={{ 
          backgroundColor: isHomePage 
            ? (scrolled 
                ? (mode === 'dark' ? 'rgba(0, 0, 0, 0.95)' : 'rgba(255, 255, 255, 0.95)')
                : 'transparent')
            : (mode === 'dark' ? 'black' : 'white'),
          color: isHomePage && !scrolled 
            ? 'white' 
            : (mode === 'dark' ? muiTheme.palette.text.primary : 'black'),
          transition: 'all 0.3s ease-in-out',
          backdropFilter: isHomePage && scrolled ? 'blur(8px)' : 'none',
          boxShadow: isHomePage && scrolled 
            ? (mode === 'dark' ? '0 4px 20px rgba(0, 0, 0, 0.5)' : '0 4px 20px rgba(0, 0, 0, 0.1)')
            : 'none',
          zIndex: 1100
        }}
      >
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ display: { xs: 'block', md: 'none' }, mr: 2 }}
            onClick={toggleMobileMenu}
          >
            <MenuIcon />
          </IconButton>
          
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Typography 
              variant="h6" 
              component={RouterLink} 
              to="/" 
              sx={{ 
                textDecoration: 'none', 
                color: 'inherit',
                display: 'flex',
                alignItems: 'center'
              }}
            >
              <RestaurantIcon sx={{ mr: 1 }} />
              {t('app.title')}
            </Typography>
          </motion.div>
          
          <Box sx={{ flexGrow: 1 }} />
          
          <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 2, alignItems: 'center' }}>
            <Button 
              color="inherit" 
              component={RouterLink} 
              to="/"
              startIcon={<HomeIcon />}
            >
              {t('navigation.home')}
            </Button>
            <Button 
              color="inherit" 
              component={RouterLink} 
              to="/ranking"
              startIcon={<EmojiEventsIcon />}
            >
              {t('navigation.ranking')}
            </Button>
            <Button 
              color="inherit" 
              component={RouterLink} 
              to="/search"
              startIcon={<SearchIcon />}
            >
              {t('navigation.search', 'Search')}
            </Button>
            
            <Tooltip title={mode === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode'}>
              <IconButton 
                color="inherit" 
                onClick={toggleTheme}
              >
                {mode === 'light' ? <Brightness4Icon /> : <Brightness7Icon />}
              </IconButton>
            </Tooltip>
            
            {isAuthenticated ? (
              <>
                <Button
                  color="inherit"
                  startIcon={<Avatar sx={{ width: 24, height: 24, bgcolor: muiTheme.palette.primary.main }}>{user?.username.charAt(0).toUpperCase()}</Avatar>}
                  onClick={handleUserMenuOpen}
                >
                  {user?.username}
                </Button>
                <Menu
                  anchorEl={userMenuAnchor}
                  open={Boolean(userMenuAnchor)}
                  onClose={handleUserMenuClose}
                >
                  <MenuItem component={RouterLink} to="/profile" onClick={handleUserMenuClose}>
                    <PersonIcon fontSize="small" sx={{ mr: 1 }} />
                    {t('navigation.profile')}
                  </MenuItem>
                  <MenuItem onClick={handleLogout}>
                    <ExitToAppIcon fontSize="small" sx={{ mr: 1 }} />
                    {t('navigation.logout')}
                  </MenuItem>
                </Menu>
              </>
            ) : (
              <>
                <Button 
                  color="inherit" 
                  component={RouterLink} 
                  to="/login"
                >
                  {t('navigation.login')}
                </Button>
                <Button 
                  variant="contained" 
                  color="secondary"
                  component={RouterLink} 
                  to="/register"
                >
                  {t('navigation.register')}
                </Button>
              </>
            )}
          </Box>
        </Toolbar>
      </AppBar>
      {isHomePage && <Toolbar />} {/* Empty toolbar to offset content only on home page */}
      
      <Drawer
        anchor="left"
        open={mobileMenuOpen}
        onClose={toggleMobileMenu}
        PaperProps={{
          sx: {
            backgroundColor: muiTheme.palette.background.paper,
            color: muiTheme.palette.text.primary
          }
        }}
      >
        <Box 
          sx={{ 
            width: 250, 
            paddingTop: 2
          }} 
          role="presentation" 
          onClick={toggleMobileMenu}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', px: 2, pb: 2 }}>
            <RestaurantIcon sx={{ mr: 1, color: muiTheme.palette.primary.main }} />
            <Typography variant="h6" color="primary">
              {t('app.title')}
            </Typography>
          </Box>
          
          <Divider sx={{ mb: 2 }} />
          
          <List>
            <ListItem component={RouterLink} to="/">
              <ListItemIcon><HomeIcon /></ListItemIcon>
              <ListItemText primary={t('navigation.home')} />
            </ListItem>
            
            <ListItem component={RouterLink} to="/ranking">
              <ListItemIcon><EmojiEventsIcon /></ListItemIcon>
              <ListItemText primary={t('navigation.ranking')} />
            </ListItem>
            
            <ListItem component={RouterLink} to="/search">
              <ListItemIcon><SearchIcon /></ListItemIcon>
              <ListItemText primary={t('navigation.search', 'Search')} />
            </ListItem>
            
            {isAuthenticated ? (
              <>
                <ListItem component={RouterLink} to="/profile">
                  <ListItemIcon><PersonIcon /></ListItemIcon>
                  <ListItemText primary={t('navigation.profile')} />
                </ListItem>
                
                <ListItem onClick={handleLogout}>
                  <ListItemIcon><ExitToAppIcon /></ListItemIcon>
                  <ListItemText primary={t('navigation.logout')} />
                </ListItem>
              </>
            ) : (
              <>
                <ListItem component={RouterLink} to="/login">
                  <ListItemIcon><PersonIcon /></ListItemIcon>
                  <ListItemText primary={t('navigation.login')} />
                </ListItem>
              </>
            )}
          </List>
        </Box>
      </Drawer>
    </>
  );
};

export default Header; 