import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  CircularProgress, 
  Card, 
  CardContent, 
  FormControl, 
  InputLabel, 
  MenuItem, 
  Select, 
  Slider, 
  Button, 
  Chip,
  Paper,
  Divider,
  TextField,
  Pagination,
  styled,
  useTheme,
  useMediaQuery
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import RestaurantRankingCard from '../components/UI/RestaurantRankingCard';
import PrefectureDropdown from '../components/UI/PrefectureDropdown';
import AreaDropdown from '../components/UI/AreaDropdown';
import GenreDropdown from '../components/UI/GenreDropdown';
import SoupBaseDropdown from '../components/UI/SoupBaseDropdown';
import { getRankedRestaurants, getAllGenres, getAllSoupBases, getAllPrefectures, getAllAreas } from '../services/api';
import { RestaurantRanking, RankingFilter, Genre, SoupBase, Prefecture, Area, PageResponse } from '../types';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import SortIcon from '@mui/icons-material/Sort';
import RestartAltIcon from '@mui/icons-material/RestartAlt';
import TuneIcon from '@mui/icons-material/Tune';

// Define the genre object interface to match API response
interface GenreObject {
  name: string;
  chineseLabel: string;
}

// Styled components for the filter options
const FilterOptionContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(1, 0),
  '& .MuiFormControl-root': {
    marginBottom: 0
  }
}));

const RankingPage: React.FC = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const isDarkMode = theme.palette.mode === 'dark';
  const [restaurants, setRestaurants] = useState<RestaurantRanking[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Pagination state
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  
  // Filter state
  const [filter, setFilter] = useState<RankingFilter>({
    sortBy: 'score',
    sortDirection: 'DESC',
    page: 0,
    size: 10,
    paginated: true
  });
  
  // Filter options
  const [genreObjects, setGenreObjects] = useState<GenreObject[]>([]);
  const [genres, setGenres] = useState<string[]>([]);
  const [soupBases, setSoupBases] = useState<string[]>([]);
  const [prefectures, setPrefectures] = useState<Prefecture[]>([]);
  const [areas, setAreas] = useState<Area[]>([]);
  const [filteredAreas, setFilteredAreas] = useState<Area[]>([]);
  
  // Selected filters
  const [selectedGenres, setSelectedGenres] = useState<string[]>([]);
  const [selectedSoupBases, setSelectedSoupBases] = useState<string[]>([]);
  const [selectedPrefecture, setSelectedPrefecture] = useState<number | null>(null);
  const [selectedArea, setSelectedArea] = useState<number | null>(null);
  const [minScore, setMinScore] = useState<number>(0);
  
  // Add a state to track if prefectures are loading
  const [loadingPrefectures, setLoadingPrefectures] = useState<boolean>(true);
  
  // Add a state to track if areas are loading
  const [loadingAreas, setLoadingAreas] = useState<boolean>(true);
  
  // Load filter options
  useEffect(() => {
    const loadFilterOptions = async () => {
      try {
        setLoadingPrefectures(true);
        console.log('Starting to load filter options...');
        
        const [genresData, soupBasesData, prefecturesData, areasData] = await Promise.all([
          getAllGenres(),
          getAllSoupBases(),
          getAllPrefectures(),
          getAllAreas()
        ]);
        
        console.log('Filter data loaded:');
        console.log('Genres:', genresData);
        console.log('Soup bases:', soupBasesData);
        console.log('Prefectures:', prefecturesData);
        console.log('Areas:', areasData);
        
        setGenreObjects(genresData);
        setGenres(genresData.map(g => g.name));
        setSoupBases(soupBasesData);
        setPrefectures(prefecturesData);
        setAreas(areasData);
        
        console.log('State updated with filter options');
      } catch (err) {
        console.error('Error loading filter options:', err);
        // Add more detailed error reporting
        if (err instanceof Error) {
          console.error('Error details:', err.message);
        }
      } finally {
        setLoadingPrefectures(false);
      }
    };
    
    loadFilterOptions();
  }, []);
  
  // Debug useEffect to verify prefectures are correctly loaded into state
  useEffect(() => {
    console.log('Prefectures state updated:', prefectures);
  }, [prefectures]);

  // Debug useEffect to verify areas are correctly loaded into state
  useEffect(() => {
    console.log('Areas state updated:', areas);
  }, [areas]);
  
  // Filter areas when prefecture changes
  useEffect(() => {
    console.log('Prefecture selection changed:', selectedPrefecture);
    
    if (selectedPrefecture) {
      console.log('Filtering areas for prefecture:', selectedPrefecture);
      
      const filtered = areas.filter(area => area.prefectureId === selectedPrefecture);
      console.log('Filtered areas:', filtered);
      
      setFilteredAreas(filtered);
      if (!filtered.some(area => area.id === selectedArea)) {
        setSelectedArea(null);
      }
    } else {
      setFilteredAreas([]);
      setSelectedArea(null);
    }
  }, [selectedPrefecture, areas, selectedArea]);
  
  // Load restaurants with filters
  useEffect(() => {
    const loadRestaurants = async () => {
      try {
        setLoading(true);
        
        // Apply filters
        const filterParams: RankingFilter = {
          ...filter
        };
        
        if (selectedGenres.length > 0) {
          // Convert strings to Genre enum values for API call
          filterParams.genres = selectedGenres.map(g => g as unknown as Genre);
        }
        
        if (selectedSoupBases.length > 0) {
          // Convert strings to SoupBase enum values for API call
          filterParams.soupBases = selectedSoupBases.map(s => s as unknown as SoupBase);
        }
        
        if (selectedPrefecture) {
          filterParams.prefectureId = selectedPrefecture;
        }
        
        if (selectedArea) {
          filterParams.areaId = selectedArea;
        }
        
        if (minScore > 0) {
          filterParams.minScore = minScore;
        }
        
        const result = await getRankedRestaurants(filterParams);
        
        if (filterParams.paginated) {
          const pageData = result as PageResponse<RestaurantRanking>;
          setRestaurants(pageData.content);
          setTotalPages(pageData.totalPages);
          setTotalItems(pageData.totalElements);
        } else {
          setRestaurants(result as RestaurantRanking[]);
        }
        
        setError(null);
      } catch (err) {
        console.error('Error loading restaurants:', err);
        setError(t('error.loadingRestaurants'));
      } finally {
        setLoading(false);
      }
    };
    
    loadRestaurants();
  }, [
    filter,
    selectedGenres,
    selectedSoupBases,
    selectedPrefecture,
    selectedArea,
    minScore,
    t
  ]);
  
  const handleSortChange = (event: any) => {
    setFilter({
      ...filter,
      sortBy: event.target.value
    });
  };
  
  const handleSortDirectionChange = (event: any) => {
    setFilter({
      ...filter,
      sortDirection: event.target.value
    });
  };
  
  const handleGenreChange = (selectedGenres: string[]) => {
    console.log('Genre selection changed:', selectedGenres);
    setSelectedGenres(selectedGenres);
  };
  
  const handleSoupBaseChange = (selectedSoupBases: string[]) => {
    console.log('Soup base selection changed:', selectedSoupBases);
    setSelectedSoupBases(selectedSoupBases);
  };
  
  const handlePrefectureChange = (event: any) => {
    const value = event.target.value;
    console.log('Prefecture selected:', value);
    console.log('Prefecture object:', prefectures.find(p => p.id === value));
    setSelectedPrefecture(value === '' ? null : value);
  };
  
  const handleAreaChange = (areaId: number | '') => {
    console.log('Area selection changed to:', areaId);
    setSelectedArea(areaId === '' ? null : areaId);
  };
  
  const handleMinScoreChange = (event: any, newValue: number | number[]) => {
    setMinScore(newValue as number);
  };
  
  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    setFilter({
      ...filter,
      page: page - 1  // API is 0-based, MUI Pagination is 1-based
    });
  };
  
  const handleResetFilters = () => {
    setSelectedGenres([]);
    setSelectedSoupBases([]);
    setSelectedPrefecture(null);
    setSelectedArea(null);
    setMinScore(0);
    setFilter({
      sortBy: 'score',
      sortDirection: 'DESC',
      page: 0,
      size: 10,
      paginated: true
    });
  };
  
  // Helper function to get the Chinese label for a genre name
  const getGenreLabel = (genreName: string): string => {
    const genre = genreObjects.find(g => g.name === genreName);
    return genre ? genre.chineseLabel : genreName;
  };
  
  return (
    <Box sx={{ px: { xs: 2, sm: 3, md: 4 } }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Typography variant="h4" component="h1" fontWeight="bold">
            {t('ranking.title')}
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 3 }}>
          {/* Filters */}
          <Box sx={{ width: { xs: '100%', md: '25%' }, mb: { xs: 3, md: 0 } }}>
            <Paper sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <TuneIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6" fontWeight="bold">
                  {t('ranking.filters')}
                </Typography>
              </Box>
              
              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel id="sort-by-label">{t('ranking.sort')}</InputLabel>
                <Select
                  labelId="sort-by-label"
                  value={filter.sortBy}
                  label={t('ranking.sort')}
                  onChange={handleSortChange}
                  startAdornment={<SortIcon sx={{ mr: 1, color: 'action.active' }} />}
                >
                  <MenuItem value="score">{t('ranking.sortOptions.score')}</MenuItem>
                  <MenuItem value="name">{t('ranking.sortOptions.name')}</MenuItem>
                </Select>
              </FormControl>
              
              <FormControl fullWidth sx={{ mb: 3 }}>
                <InputLabel id="sort-direction-label">{t('ranking.sort')}</InputLabel>
                <Select
                  labelId="sort-direction-label"
                  value={filter.sortDirection}
                  label={t('ranking.sort')}
                  onChange={handleSortDirectionChange}
                >
                  <MenuItem value="DESC">{t('ranking.sortOptions.descending')}</MenuItem>
                  <MenuItem value="ASC">{t('ranking.sortOptions.ascending')}</MenuItem>
                </Select>
              </FormControl>
              
              <Divider sx={{ my: 3 }} />
              
              <FilterOptionContainer>
                <PrefectureDropdown 
                  value={selectedPrefecture || ''}
                  onChange={(value) => setSelectedPrefecture(value === '' ? null : value)}
                />
              </FilterOptionContainer>
              
              <FilterOptionContainer>
                <AreaDropdown 
                  value={selectedArea || ''}
                  onChange={handleAreaChange}
                  prefectureId={selectedPrefecture}
                />
              </FilterOptionContainer>
              
              <FilterOptionContainer>
                <GenreDropdown
                  value={selectedGenres}
                  onChange={handleGenreChange}
                />
              </FilterOptionContainer>
              
              <FilterOptionContainer>
                <SoupBaseDropdown
                  value={selectedSoupBases}
                  onChange={handleSoupBaseChange}
                />
              </FilterOptionContainer>
              
              <Button
                fullWidth
                variant="outlined"
                color="secondary"
                onClick={handleResetFilters}
                startIcon={<RestartAltIcon />}
              >
                {t('ranking.resetFilters')}
              </Button>
            </Paper>
          </Box>
          
          {/* Restaurant List */}
          <Box sx={{ width: { xs: '100%', md: '75%' } }}>
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
            ) : restaurants.length === 0 ? (
              <Card sx={{ p: 4, textAlign: 'center' }}>
                <CardContent>
                  <Typography>{t('ranking.noResults')}</Typography>
                </CardContent>
              </Card>
            ) : (
              <Box>
                {/* Filter chips */}
                {(selectedGenres.length > 0 || selectedSoupBases.length > 0 || selectedPrefecture || selectedArea || minScore > 0) && (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 3 }}>
                    <FilterAltIcon color="action" />
                    
                    {selectedGenres.map((genre) => (
                      <Chip 
                        key={genre} 
                        label={getGenreLabel(genre)} 
                        onDelete={() => setSelectedGenres(selectedGenres.filter(g => g !== genre))}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    ))}
                    
                    {selectedSoupBases.map((base) => (
                      <Chip 
                        key={base} 
                        label={base} 
                        onDelete={() => setSelectedSoupBases(selectedSoupBases.filter(b => b !== base))}
                        size="small"
                        color="secondary"
                      />
                    ))}
                    
                    {selectedPrefecture && (
                      <Chip 
                        label={prefectures.find(p => p.id === selectedPrefecture)?.name} 
                        onDelete={() => setSelectedPrefecture(null)}
                        size="small"
                      />
                    )}
                    
                    {selectedArea && (
                      <Chip 
                        label={areas.find(a => a.id === selectedArea)?.name} 
                        onDelete={() => setSelectedArea(null)}
                        size="small"
                      />
                    )}
                    
                    {minScore > 0 && (
                      <Chip 
                        label={`${t('ranking.minScore')}: ${minScore}`} 
                        onDelete={() => setMinScore(0)}
                        size="small"
                      />
                    )}
                  </Box>
                )}
                
                {/* Restaurant cards section */}
                {restaurants.map((restaurant, index) => (
                  <Box key={restaurant.id} mb={3}>
                    <RestaurantRankingCard
                      restaurant={restaurant}
                      rank={index + 1 + (filter.page || 0) * (filter.size || 10)}
                      delay={index * 0.05}
                      sortBy={filter.sortBy}
                      isDarkMode={isDarkMode}
                    />
                  </Box>
                ))}
                
                {/* Pagination */}
                {filter.paginated && totalPages > 0 && (
                  <Box sx={{ mt: 4, mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        {t('ranking.showing')} {(filter.page || 0) * (filter.size || 10) + 1} - 
                        {Math.min((filter.page || 0) * (filter.size || 10) + restaurants.length, totalItems)}，
                        {t('ranking.of')} {totalItems} {t('ranking.restaurants')}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                      <Pagination
                        count={totalPages}
                        page={(filter.page || 0) + 1} // Convert 0-based API page to 1-based MUI Pagination
                        onChange={handlePageChange}
                        color="primary"
                        size="large"
                        showFirstButton
                        showLastButton
                      />
                    </Box>
                  </Box>
                )}
              </Box>
            )}
          </Box>
        </Box>
      </motion.div>
    </Box>
  );
};

export default RankingPage; 