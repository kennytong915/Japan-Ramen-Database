import React, { useEffect, useState } from "react";
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
import { RestaurantRanking } from '../../types';
import PlaceIcon from '@mui/icons-material/Place';
import RamenDiningIcon from '@mui/icons-material/RamenDining';
import { useNavigate } from "react-router-dom";
import { getLatestPhotoForRestaurant } from "../../services/api";

interface RestaurantRankingCardProps {
  restaurant: RestaurantRanking;
  rank: number;
  delay?: number;
  sortBy?: string;
  isDarkMode?: boolean;
}

const RestaurantRankingCard: React.FC<RestaurantRankingCardProps> = ({ 
  restaurant, 
  rank, 
  delay = 0, 
  sortBy = 'score', 
  isDarkMode = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const navigate = useNavigate();
  const [thumbnailUrl, setThumbnailUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [imageError, setImageError] = useState(false);
  
  const location = restaurant.prefectureName && restaurant.areaName
    ? `${restaurant.prefectureName}, ${restaurant.areaName}`
    : restaurant.prefectureName || restaurant.areaName || '';

  // Helper function to handle genre display - can work with both string and object formats
  const getGenreLabel = (genre: any): string => {
    if (typeof genre === 'object' && genre !== null && 'chineseLabel' in genre) {
      return genre.chineseLabel;
    } else if (typeof genre === 'object' && genre !== null && 'name' in genre) {
      return genre.name;
    } else {
      return String(genre);
    }
  };

  // Helper function to handle soup base display - can work with both string and object formats
  const getSoupBaseLabel = (base: any): string => {
    if (typeof base === 'object' && base !== null && 'chineseLabel' in base) {
      return base.chineseLabel;
    } else if (typeof base === 'object' && base !== null && 'name' in base) {
      return base.name;
    } else {
      return String(base);
    }
  };
  
  // Get ranking color
  const getRankColor = (rank: number) => {
    if (rank === 1) return '#FFD700'; // Gold
    if (rank === 2) return '#C0C0C0'; // Silver
    if (rank === 3) return '#CD7F32'; // Bronze
    return isDarkMode ? '#8a8a8a' : '#888'; // Deep gray for dark mode
  };

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
  const rankColor = getRankColor(rank);

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
            {/* Name and rank container */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.75 }}>
              {/* Rank number */}
              <Typography 
                variant="h6" 
                component="span" 
                sx={{ 
                  fontWeight: 700, 
                  color: rankColor,
                  mr: 1.5,
                  fontSize: isMobile ? '1.1rem' : '1.25rem',
                  lineHeight: 1,
                  minWidth: '1.5rem'
                }}
              >
                {rank}
              </Typography>
              
              {/* Restaurant name */}
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
            
            <Box sx={{ 
              display: 'flex', 
              flexWrap: 'wrap', 
              gap: 1
            }}>
              {restaurant.genres && restaurant.genres.map((genre, index) => {
                const chipLabel = getGenreLabel(genre);
                return (
                  <Chip 
                    key={`genre-${index}`}
                    label={chipLabel}
                    size="small" 
                    color="primary"
                    variant="outlined"
                  />
                );
              })}
              {restaurant.soupBases && restaurant.soupBases.map((base, index) => {
                const chipLabel = getSoupBaseLabel(base);
                return (
                  <Chip 
                    key={`soup-${index}`}
                    label={chipLabel}
                    size="small" 
                    color="secondary"
                    variant="outlined"
                  />
                );
              })}
            </Box>
          </CardContent>
          
          {/* Score section - using flex */}
          {(restaurant.score && (sortBy === 'score' || sortBy === 'name')) && (
            <Box sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              pr: isMobile ? 2 : 3,
              pl: 0
            }}>
              <Box sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Typography 
                  variant="body1" 
                  component="span"
                  sx={{ 
                    fontWeight: 700,
                    fontSize: isMobile ? '1.3rem' : '1.5rem',
                    lineHeight: 1.1,
                    color: isDarkMode ? '#f05545' : theme.palette.primary.main
                  }}
                >
                  {restaurant.score.toFixed(1)}
                </Typography>
                <Typography
                  variant="caption"
                  component="span"
                  sx={{
                    fontSize: '0.7rem',
                    color: isDarkMode ? 'rgba(255,255,255,0.6)' : 'rgba(0,0,0,0.6)',
                    letterSpacing: '0.05em',
                    textTransform: 'uppercase'
                  }}
                >
                  綜合評分
                </Typography>
              </Box>
            </Box>
          )}
        </CardActionArea>
      </Card>
    </motion.div>
  );
};

export default RestaurantRankingCard; 