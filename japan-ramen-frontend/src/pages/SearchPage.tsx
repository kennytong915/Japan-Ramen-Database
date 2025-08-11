import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Container,
  TextField,
  Button,
  CircularProgress,
  Alert,
  Pagination,
  Paper
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useTranslation } from 'react-i18next';
import RestaurantRankingCard from '../components/UI/RestaurantRankingCard';
import axios from 'axios';
import { RestaurantRanking, PageResponse } from '../types';

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const SearchPage: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  
  // Parse search query from URL if any
  const queryParams = new URLSearchParams(location.search);
  const initialSearchTerm = queryParams.get('name') || '';
  
  const [searchTerm, setSearchTerm] = useState<string>(initialSearchTerm);
  const [restaurants, setRestaurants] = useState<RestaurantRanking[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const pageSize = 10;
  
  // Function to search restaurants by name
  const searchRestaurants = async (term: string, pageNum: number) => {
    if (!term.trim()) {
      setRestaurants([]);
      setError(null);
      return;
    }
    
    setLoading(true);
    try {
      // Use the API directly since we don't have a dedicated function for name search
      const response = await api.get('/restaurants', { 
        params: {
          name: term.trim(),
          page: pageNum,
          size: pageSize,
          paginated: true
        }
      });
      
      const data: PageResponse<RestaurantRanking> = response.data;
      setRestaurants(data.content);
      setTotalPages(data.totalPages);
      setError(null);
      
      // Update URL with search parameters
      navigate(`/search?name=${encodeURIComponent(term.trim())}&page=${pageNum + 1}`, { replace: true });
    } catch (err) {
      console.error('Error searching restaurants:', err);
      setError(t('error.searchFailed'));
      setRestaurants([]);
    } finally {
      setLoading(false);
    }
  };
  
  // Handle search button click
  const handleSearch = () => {
    setPage(0); // Reset to first page on new search
    searchRestaurants(searchTerm, 0);
  };
  
  // Handle page change
  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    const pageIndex = value - 1; // Convert to 0-based index
    setPage(pageIndex);
    searchRestaurants(searchTerm, pageIndex);
  };
  
  // Handle form submission on Enter key
  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };
  
  // Execute search on initial load if there's a search term in the URL
  useEffect(() => {
    if (initialSearchTerm) {
      const pageParam = parseInt(queryParams.get('page') || '1', 10);
      const pageIndex = Math.max(0, pageParam - 1); // Convert to 0-based index, ensure >= 0
      setPage(pageIndex);
      searchRestaurants(initialSearchTerm, pageIndex);
    }
  }, []);
  
  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {t('search.title', 'Search Restaurants')}
        </Typography>
        
        {/* Search Form */}
        <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
          <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 2, alignItems: 'center' }}>
            <Box sx={{ flex: { xs: '1 1 100%', md: '3 1 0%' } }}>
              <TextField
                fullWidth
                label={t('search.nameLabel', 'Restaurant Name')}
                variant="outlined"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder={t('search.namePlaceholder', 'Enter restaurant name...')}
                InputProps={{
                  startAdornment: <SearchIcon color="action" sx={{ mr: 1 }} />
                }}
              />
            </Box>
            <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 0%' } }}>
              <Button 
                fullWidth 
                variant="contained" 
                color="primary" 
                onClick={handleSearch}
                disabled={loading}
                sx={{ py: 1.5 }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : t('search.button', 'Search')}
              </Button>
            </Box>
          </Box>
        </Paper>
        
        {/* Search Results */}
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}
        
        {!loading && restaurants.length === 0 && searchTerm && !error && (
          <Alert severity="info" sx={{ mb: 3 }}>
            {t('search.noResults', 'No restaurants found matching your search.')}
          </Alert>
        )}
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {restaurants.map((restaurant, idx) => (
              <Box key={restaurant.id}>
                <RestaurantRankingCard 
                  restaurant={restaurant} 
                  rank={idx + 1 + (page * pageSize)}
                />
              </Box>
            ))}
          </Box>
        )}
        
        {/* Pagination */}
        {restaurants.length > 0 && totalPages > 1 && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <Pagination 
              count={totalPages} 
              page={page + 1} 
              onChange={handlePageChange}
              color="primary"
              size="large"
            />
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default SearchPage; 