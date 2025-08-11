import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Button, 
  CircularProgress, 
  Card, 
  CardContent,
  Container,
  Paper,
  InputBase,
  IconButton,
  Grid,
  useTheme,
  useMediaQuery,
  TextField,
  SelectChangeEvent
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import RestaurantCard from '../components/UI/RestaurantCard';
import { getFrontpageCards } from '../services/api';
import { RestaurantCard as RestaurantCardType } from '../types';
import RamenDiningIcon from '@mui/icons-material/RamenDining';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import PrefectureDropdown from '../components/UI/PrefectureDropdown';
import GenreDropdown from '../components/UI/GenreDropdown';
import SoupBaseDropdown from '../components/UI/SoupBaseDropdown';
import AreaDropdown from '../components/UI/AreaDropdown';

// Import the hero image
import heroBackground from '../assets/images/image0.jpg';

const HeroSection = styled(Box)(({ theme }) => ({
  backgroundImage: `url(${heroBackground})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  position: 'relative',
  height: '90vh',
  width: '100%',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: '#fff',
  marginTop: '-64px', 
  paddingTop: '64px',
  zIndex: 0, // Ensure the hero section is below the navbar
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)', // Overlay to make text more readable
    zIndex: 1,
  },
}));

const SearchContainer = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(0),
  display: 'flex',
  flexDirection: 'column',
  width: '100%',
  maxWidth: 1200,
  background: 'rgba(255, 255, 255, 0.05)',
  color: '#fff',
  borderRadius: 8,
  boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
  zIndex: 2,
  [theme.breakpoints.up('md')]: {
    flexDirection: 'row',
  },
  '& .MuiInputBase-root': {
    color: '#fff',
  },
  '& .MuiInputLabel-root': {
    color: 'rgba(255, 255, 255, 0.7)',
  },
  '& .MuiOutlinedInput-notchedOutline': {
    borderColor: 'rgba(255, 255, 255, 0.5)',
  },
  '& .MuiSvgIcon-root': {
    color: 'rgba(255, 255, 255, 0.7)',
  },
}));

const MobileSearchField = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(1.5),
  marginBottom: theme.spacing(3.5),
  width: '90%',
  marginLeft: 'auto',
  marginRight: 'auto',
  borderRadius: 8,
  boxShadow: '0 2px 8px rgba(0, 0, 0, 0.12)',
  display: 'flex',
  alignItems: 'center',
  background: '#fff',
  color: 'rgba(0, 0, 0, 0.87)',
}));

const SearchButton = styled(Button)(({ theme }) => ({
  background: theme.palette.success.main,
  color: '#fff',
  fontWeight: 'bold',
  padding: theme.spacing(1.5, 3),
  borderRadius: 4,
  '&:hover': {
    background: theme.palette.success.dark,
  },
  [theme.breakpoints.down('md')]: {
    width: '100%',
    marginTop: theme.spacing(1),
    backgroundColor: '#82ca9c', // Foogra green color
    '&:hover': {
      backgroundColor: '#6db888',
    },
  },
}));

const ContentWrapper = styled(Box)(({ theme }) => ({
  position: 'relative',
  zIndex: 2,
  width: '100%',
  textAlign: 'center',
  paddingTop: '30px', // Push content down a bit more
}));

const HomePage: React.FC = () => {
  const { t } = useTranslation();
  const [restaurants, setRestaurants] = useState<RestaurantCardType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [prefecture, setPrefecture] = useState<number | ''>('');
  const [area, setArea] = useState<number | ''>('');
  const [soupBase, setSoupBase] = useState<string[]>([]);
  
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  useEffect(() => {
    const loadRestaurants = async () => {
      try {
        setLoading(true);
        const data = await getFrontpageCards();
        setRestaurants(data);
        setError(null);
      } catch (err) {
        console.error('Error loading restaurants:', err);
        setError(t('error.loadingRestaurants'));
      } finally {
        setLoading(false);
      }
    };
    
    loadRestaurants();
  }, [t]);

  const handleSearch = () => {
    // Build query parameters for the search
    const params = new URLSearchParams();
    if (prefecture !== '') params.append('prefectureId', prefecture.toString());
    if (area !== '') params.append('areaId', area.toString());
    if (soupBase.length > 0) {
      soupBase.forEach(s => params.append('soupBases', s));
    }
    
    // Navigate to the ranking page with search parameters
    navigate(`/ranking?${params.toString()}`);
  };
  
  // Render desktop search UI
  const renderDesktopSearch = () => (
    <SearchContainer elevation={1}>
      <Box sx={{ display: 'flex', flexDirection: 'row', width: '100%', alignItems: 'center' }}>
        <Box sx={{ flex: '2.5 1 0%', p: 1 }}>
          <PrefectureDropdown 
            value={prefecture} 
            onChange={(prefectureId) => setPrefecture(prefectureId)}
            fullWidth
          />
        </Box>
        
        <Box sx={{ flex: '2.5 1 0%', p: 1 }}>
          <AreaDropdown 
            value={area} 
            onChange={(areaId) => setArea(areaId)}
            prefectureId={prefecture === '' ? null : prefecture}
            fullWidth
          />
        </Box>
        
        <Box sx={{ flex: '2.5 1 0%', p: 1 }}>
          <SoupBaseDropdown 
            value={soupBase} 
            onChange={(soupBases) => setSoupBase(soupBases)}
            fullWidth
          />
        </Box>
        
        <Box sx={{ flex: '1.5 1 0%', p: 1 }}>
          <SearchButton 
            fullWidth 
            variant="contained" 
            onClick={handleSearch}
          >
            Search
          </SearchButton>
        </Box>
      </Box>
    </SearchContainer>
  );

  const mobileSearchPaperStyle = {
    mb: 1,
    borderRadius: 1,
    overflow: 'hidden',
    bgcolor: 'rgba(255, 255, 255, 0.05)',
    color: '#fff',
    '& .MuiInputBase-root': {
      color: '#fff',
    },
    '& .MuiInputLabel-root': {
      color: 'rgba(255, 255, 255, 0.7)',
    },
    '& .MuiOutlinedInput-notchedOutline': {
      borderColor: 'rgba(255, 255, 255, 0.5)',
    },
    '& .MuiSvgIcon-root': {
      color: 'rgba(255, 255, 255, 0.7)',
    },
  };
  
  // Render mobile search UI (Foogra style)
  const renderMobileSearch = () => (
    <Box 
      sx={{ 
        width: '100%', 
        maxWidth: '500px',
        margin: '0 auto',
        backgroundColor: 'transparent',
        borderRadius: 2,
        padding: 0,
      }}
    >
      <Box sx={{ position: 'relative' }}>
        <Paper 
          elevation={2}
          sx={mobileSearchPaperStyle}
        >
          <PrefectureDropdown 
            value={prefecture} 
            onChange={(prefectureId) => setPrefecture(prefectureId)}
            fullWidth
          />
        </Paper>
        
        <Paper 
          elevation={2}
          sx={mobileSearchPaperStyle}
        >
          <AreaDropdown 
            value={area} 
            onChange={(areaId) => setArea(areaId)}
            prefectureId={prefecture === '' ? null : prefecture}
            fullWidth
          />
        </Paper>
        
        <Paper 
          elevation={2}
          sx={{
            ...mobileSearchPaperStyle,
            mb: 0 // No margin bottom for the last item
          }}
        >
          <SoupBaseDropdown 
            value={soupBase} 
            onChange={(soupBases) => setSoupBase(soupBases)}
            fullWidth
          />
        </Paper>
      </Box>
      
      <Box sx={{ mt: 3 }}>
        <SearchButton 
          fullWidth 
          variant="contained" 
          onClick={handleSearch}
          sx={{ py: 1.5, fontSize: '1rem', textTransform: 'none', boxShadow: 3, width: '90%', mx: 'auto', display: 'block', borderRadius: 1 }}
        >
          Search
        </SearchButton>
      </Box>
    </Box>
  );
  
  return (
    <Box>
      {/* Hero Section */}
      <HeroSection>
        <ContentWrapper>
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
          >
            <Typography variant="h2" component="h1" fontWeight="bold" gutterBottom>
              發掘日本最頂級的拉麵
            </Typography>
            <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
            一個專為拉麵愛好者打造的指南
            </Typography>
            
            <Container maxWidth="lg">
              {isMobile ? renderMobileSearch() : renderDesktopSearch()}
            </Container>
          </motion.div>
        </ContentWrapper>
      </HeroSection>
      
      {/* Popular Categories / Ramen Listings */}
      <Container maxWidth="lg">
        <Box sx={{ my: 6 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
            <Typography variant="h4" component="h2" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center' }}>
              <RamenDiningIcon sx={{ mr: 1, color: 'primary.main' }} />
              {t('home.featured')}
            </Typography>
            
            <Button 
              variant="outlined"
              onClick={() => navigate('/ranking')}
            >
              {t('home.viewAll')}
            </Button>
          </Box>
          
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Card sx={{ p: 2, backgroundColor: 'error.dark' }}>
              <CardContent>
                <Typography color="error.contrastText">{error}</Typography>
              </CardContent>
            </Card>
          ) : (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', margin: -1.5 }}>
              {restaurants.map((restaurant, index) => (
                <Box 
                  key={restaurant.id} 
                  sx={{ 
                    width: { xs: '100%', sm: '50%', md: '33.33%' }, 
                    padding: 1.5 
                  }}
                >
                  <RestaurantCard restaurant={restaurant} delay={index} />
                </Box>
              ))}
            </Box>
          )}
        </Box>
      </Container>
    </Box>
  );
};

export default HomePage; 