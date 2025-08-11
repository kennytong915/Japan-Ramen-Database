import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Card, 
  CardContent, 
  CardActionArea, 
  Typography, 
  Box, 
  Chip, 
  useTheme, 
  useMediaQuery,
  Skeleton
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { RestaurantCard as RestaurantCardType } from '../../types';
import PlaceIcon from '@mui/icons-material/Place';
import RamenDiningIcon from '@mui/icons-material/RamenDining';
import { getLatestPhotoForRestaurant } from "../../services/api";
import { useTheme as useAppTheme } from '../../contexts/ThemeContext';

interface RestaurantCardProps {
  restaurant: RestaurantCardType;
  delay?: number;
}

const RestaurantCard: React.FC<RestaurantCardProps> = ({ restaurant, delay = 0 }) => {
  const { t } = useTranslation();
  const theme = useTheme();
  const { mode } = useAppTheme();
  const isDarkMode = mode === 'dark';
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [thumbnailUrl, setThumbnailUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [imageError, setImageError] = useState(false);
  
  const location = restaurant.prefectureName && restaurant.areaName
    ? `${restaurant.prefectureName}, ${restaurant.areaName}`
    : restaurant.prefectureName || restaurant.areaName || '';

  useEffect(() => {
    const fetchThumbnail = async () => {
      setIsLoading(true);
      setImageError(false);
      try {
        const photoUrl = await getLatestPhotoForRestaurant(restaurant.id);
        setThumbnailUrl(photoUrl);
      } catch (error) {
        console.error("Error fetching thumbnail:", error);
        setImageError(true);
      } finally {
        setIsLoading(false);
      }
    };

    fetchThumbnail();
  }, [restaurant.id]);

  const handleImageError = () => {
    setImageError(true);
    setIsLoading(false);
  };
  
  const imageSize = isMobile ? 120 : 160;

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3, delay: delay * 0.1 }}
      style={{ width: '100%' }}
    >
      <Card 
        elevation={0}
        sx={{ 
          borderRadius: 1,
          overflow: 'hidden',
          transition: 'all 0.2s ease',
          '&:hover': {
            backgroundColor: isDarkMode ? 'rgba(30,30,30,0.95)' : 'rgba(245,245,245,0.8)'
          },
          display: 'flex',
          backgroundColor: isDarkMode ? '#1e1e1e' : 'rgba(255,255,255,0.7)',
          border: `1px solid ${isDarkMode ? 'rgba(45,45,45,0.8)' : 'rgba(0,0,0,0.05)'}`,
          position: 'relative',
          height: 'auto',
          minHeight: imageSize
        }}
      >
        <CardActionArea 
          component={RouterLink} 
          to={`/restaurants/${restaurant.id}`}
          sx={{ 
            display: 'flex', 
            flexDirection: 'row', 
            width: '100%',
            alignItems: 'stretch'
          }}
        >
          {/* Restaurant image section */}
          <Box sx={{ 
            width: imageSize,
            minWidth: imageSize,
            maxWidth: imageSize,
            aspectRatio: '1/1',
            position: 'relative',
            overflow: 'hidden'
          }}>
            {(!isLoading && thumbnailUrl && !imageError) ? (
              <Box
                component="img"
                src={thumbnailUrl}
                alt={restaurant.name}
                sx={{ 
                  width: '100%',
                  height: '100%',
                  objectFit: 'cover',
                  objectPosition: 'center'
                }}
                onError={handleImageError}
              />
            ) : isLoading ? (
              <Skeleton 
                variant="rectangular" 
                width="100%" 
                height="100%" 
                animation="wave"
                sx={{ 
                  bgcolor: isDarkMode ? 'rgba(50,50,50,0.7)' : undefined
                }}
              />
            ) : (
              <Box 
                sx={{ 
                  height: '100%',
                  width: '100%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  backgroundColor: isDarkMode ? 'rgba(40,40,40,0.6)' : 'rgba(0,0,0,0.03)'
                }}
              >
                <RamenDiningIcon sx={{ fontSize: isMobile ? 40 : 50, color: isDarkMode ? 'rgba(100,100,100,0.6)' : 'rgba(0,0,0,0.1)' }} />
              </Box>
            )}
          </Box>

          {/* Content section */}
          <CardContent sx={{ 
            flex: 1,
            pt: 2, 
            pb: '16px !important',
            pr: isMobile ? 2 : 3,
            pl: 2,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center'
          }}>
            {/* Restaurant name */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.75 }}>
              <Typography 
                variant="h6" 
                component="h2" 
                sx={{ 
                  fontWeight: 600, 
                  fontSize: isMobile ? '1rem' : '1.1rem',
                  lineHeight: 1.2,
                  color: isDarkMode ? '#f0f0f0' : 'inherit'
                }}
              >
                {restaurant.name}
              </Typography>
            </Box>
            
            {location && (
              <Box sx={{ 
                display: 'flex', 
                alignItems: 'center', 
                mb: 1.5,
                color: isDarkMode ? 'rgba(180,180,180,0.8)' : 'text.secondary' 
              }}>
                <PlaceIcon fontSize="small" sx={{ mr: 0.5, fontSize: '0.8rem' }} />
                <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                  {location}
                </Typography>
              </Box>
            )}
          </CardContent>
        </CardActionArea>
      </Card>
    </motion.div>
  );
};

export default RestaurantCard; 