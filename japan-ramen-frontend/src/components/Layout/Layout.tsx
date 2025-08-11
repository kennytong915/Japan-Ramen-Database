import React from 'react';
import { Box, Container, CssBaseline } from '@mui/material';
import { motion } from 'framer-motion';
import { useLocation } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const location = useLocation();
  const isHomePage = location.pathname === '/';
  
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
        backgroundColor: 'background.default',
      }}
    >
      <CssBaseline />
      <Header />
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
        style={{ flex: 1 }}
      >
        <Container 
          component="main" 
          maxWidth={false} 
          disableGutters 
          sx={{ 
            py: isHomePage ? 0 : 4,
            mt: isHomePage ? 0 : 0
          }}
        >
          {children}
        </Container>
      </motion.div>
      <Footer />
    </Box>
  );
};

export default Layout; 