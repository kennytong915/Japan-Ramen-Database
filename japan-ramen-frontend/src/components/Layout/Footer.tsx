import React from 'react';
import { Box, Container, Typography, Link, Divider } from '@mui/material';
import { useTranslation } from 'react-i18next';

const Footer: React.FC = () => {
  const { t } = useTranslation();
  const currentYear = new Date().getFullYear();

  return (
    <Box
      component="footer"
      sx={{
        py: 3,
        mt: 'auto',
        backgroundColor: 'background.paper',
      }}
    >
      <Divider />
      <Container maxWidth="lg">
        <Box
          sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            justifyContent: 'space-between',
            alignItems: 'center',
            py: 2,
          }}
        >
          <Typography variant="h6" color="text.primary" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
            {t('app.title')}
          </Typography>

          <Box sx={{ display: 'flex', gap: 2, mb: { xs: 2, md: 0 } }}>
            <Link href="#" color="text.secondary" sx={{ textDecoration: 'none' }}>
              {t('navigation.home')}
            </Link>
            <Link href="#" color="text.secondary" sx={{ textDecoration: 'none' }}>
              {t('navigation.ranking')}
            </Link>
          </Box>
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'center', pt: 2 }}>
          <Typography variant="body2" color="text.secondary" align="center">
            © {currentYear} {t('app.title')}
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer; 