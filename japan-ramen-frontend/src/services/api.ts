import axios from 'axios';
import { 
  Restaurant, 
  RestaurantCard,
  RestaurantRanking,
  Comment,
  CommentRequest,
  LoginRequest,
  RegistrationRequest,
  User,
  RankingFilter,
  Genre,
  SoupBase,
  Prefecture,
  Area
} from '../types';

// API base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    
    console.log(`Request to ${config.url}`);
    
    if (token) {
      // Ensure the token is properly formatted with Bearer prefix
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log(`Auth header set for ${config.url}, token starting with: ${token.substring(0, 10)}...`);
    } else {
      console.warn(`No auth token available for request to ${config.url}`);
    }
    
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for debugging auth issues
api.interceptors.response.use(
  (response) => {
    console.log(`Response from ${response.config.url}: ${response.status}`);
    return response;
  },
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      console.error(`Error ${error.response.status} from ${error.config?.url}:`, error.response.data);
      
      // Special handling for auth errors
      if (error.response.status === 401) {
        console.error('Authentication error - token may be invalid or expired');
        console.error('Request headers:', error.config?.headers);
      }
    }
    return Promise.reject(error);
  }
);

// Auth services
export const login = async (credentials: LoginRequest): Promise<string> => {
  // Using axios directly to bypass the baseURL for this specific endpoint
  console.log('Sending login request to:', 'http://localhost:8080/auth/login');
  
  const response = await axios.post('http://localhost:8080/auth/login', credentials, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  
  console.log('Login response status:', response.status);
  console.log('Login response type:', typeof response.data);
  
  // Make sure we're properly extracting the token from the response
  let token = '';
  
  if (typeof response.data === 'string') {
    // If response.data is a string, it's likely the token itself
    token = response.data;
    console.log('Token received as string');
  } else if (typeof response.data === 'object' && response.data !== null) {
    // If it's an object, look for common token field names
    if (response.data.token) token = response.data.token;
    else if (response.data.access_token) token = response.data.access_token;
    else if (response.data.jwt) token = response.data.jwt;
    else if (response.data.accessToken) token = response.data.accessToken;
    
    console.log('Response data keys:', Object.keys(response.data));
    console.log('Token extracted from object with key:', token ? 'Found token' : 'No token found in object');
  }
  
  // Store the token in localStorage
  if (token) {
    localStorage.setItem('token', token);
    console.log('Token stored in localStorage, first 15 chars:', token.substring(0, 15) + '...');
  } else {
    console.error('No token found in response', response.data);
  }
  
  return token;
};

export const register = async (userData: RegistrationRequest): Promise<User> => {
  const response = await api.post('/users/register', userData);
  return response.data;
};

export const getCurrentUser = async (): Promise<User> => {
  // Log the token to debug authentication issues
  const token = localStorage.getItem('token');
  console.log('Token when fetching user:', token ? `${token.substring(0, 15)}...` : 'No token');
  
  // Only use the /users/me endpoint as specified
  const response = await api.get('/users/me');
  return response.data;
};

export const getRecaptchaSiteKey = async (): Promise<string> => {
  const response = await api.get('/recaptcha/sitekey');
  return response.data.siteKey;
};

// Restaurant services
export const getTopRestaurants = async (limit: number = 10): Promise<RestaurantRanking[]> => {
  const response = await api.get(`/ranking/top?limit=${limit}`);
  return response.data;
};

export const getRestaurantById = async (id: number): Promise<Restaurant> => {
  const response = await api.get(`/restaurants/${id}`);
  return response.data;
};

export const getFrontpageCards = async (): Promise<RestaurantCard[]> => {
  const response = await api.get('/restaurants/frontpage-cards');
  return response.data;
};

// Ranking services
export const getRankedRestaurants = async (filters: RankingFilter): Promise<any> => {
  // Convert filters to query params
  const params = new URLSearchParams();
  
  if (filters.prefectureId) params.append('prefectureId', filters.prefectureId.toString());
  if (filters.areaId) params.append('areaId', filters.areaId.toString());
  if (filters.genres && filters.genres.length > 0) {
    filters.genres.forEach(genre => {
      params.append('genres', genre);
    });
  }
  if (filters.soupBases && filters.soupBases.length > 0) {
    filters.soupBases.forEach(base => {
      params.append('soupBases', base);
    });
  }
  if (filters.minScore) params.append('minScore', filters.minScore.toString());
  if (filters.name) params.append('name', filters.name);
  if (filters.reservationRequired !== undefined) params.append('reservationRequired', filters.reservationRequired.toString());
  if (filters.sortBy) params.append('sortBy', filters.sortBy);
  if (filters.sortDirection) params.append('sortDirection', filters.sortDirection);
  
  // Add pagination params
  params.append('page', filters.page !== undefined ? filters.page.toString() : '0');
  params.append('size', filters.size ? filters.size.toString() : '10');
  params.append('paginated', filters.paginated?.toString() || 'false');
  
  const response = await api.get(`/ranking?${params.toString()}`);
  return response.data;
};

export const getAllGenres = async (): Promise<any[]> => {
  try {
    console.log('Fetching genres...');
    const response = await api.get('/restaurants/genres');
    console.log('Genre response received:', response.status);
    console.log('Genre data:', response.data);
    
    if (!Array.isArray(response.data)) {
      console.error('Genre data is not an array:', response.data);
      return [];
    }
    
    // Return the complete genre objects with name and chineseLabel
    return response.data;
  } catch (error) {
    console.error('Error fetching genres:', error);
    return [];
  }
};

export const getAllSoupBases = async (): Promise<SoupBase[]> => {
  const response = await api.get('/restaurants/soupbases');
  return response.data;
};

export const getAllPrefectures = async (): Promise<Prefecture[]> => {
  const response = await api.get('/address/prefectures');
  return response.data;
};

export const getAllAreas = async (): Promise<Area[]> => {
  const response = await api.get('/restaurants/areas');
  return response.data;
};

// Comment services
export const getCommentsByRestaurant = async (restaurantId: number): Promise<Comment[]> => {
  const response = await api.get(`/comments/restaurant/${restaurantId}`);
  return response.data;
};

/**
 * Get paginated comments for a restaurant
 */
export const getPaginatedCommentsByRestaurant = async (
  restaurantId: number, 
  page: number = 0, 
  size: number = 5
): Promise<any> => {
  const response = await api.get(
    `/comments/restaurant/${restaurantId}/paginated?page=${page}&size=${size}&sort=createdAt,desc`
  );
  return response.data;
};

/**
 * Get all photos from a restaurant's comments
 */
export const getAllPhotosFromRestaurant = async (restaurantId: number): Promise<Array<{url: string, commentUsername: string}>> => {
  const response = await api.get(`/comments/restaurant/${restaurantId}/photos`);
  
  // Transform the response to match the expected interface
  // Assuming the backend returns an array of objects with { url, username }
  return response.data.map((item: {url: string, username: string}) => ({
    url: item.url,
    commentUsername: item.username
  }));
};

/**
 * Get the latest photo URL for a restaurant (for thumbnail)
 */
export const getLatestPhotoForRestaurant = async (restaurantId: number): Promise<string | null> => {
  try {
    const response = await api.get(`/comments/restaurant/${restaurantId}/latest-photo`);
    return response.data?.photoUrl || null;
  } catch (error) {
    console.error(`Error fetching latest photo for restaurant ${restaurantId}:`, error);
    return null;
  }
};

export const createComment = async (commentData: CommentRequest): Promise<Comment> => {
  const response = await api.post('/comments', commentData);
  return response.data;
};

export const updateComment = async (commentId: number, commentData: CommentRequest): Promise<Comment> => {
  const response = await api.put(`/comments/${commentId}`, commentData);
  return response.data;
};

export const deleteComment = async (commentId: number): Promise<void> => {
  await api.delete(`/comments/${commentId}`);
};

/**
 * Create a new comment with photos in a single request
 */
export const createCommentWithPhotos = async (commentData: CommentRequest, photos: File[]): Promise<Comment> => {
  try {
    const token = localStorage.getItem('token');
    
    // Create form data
    const formData = new FormData();
    
    // Add comment data as JSON
    formData.append('commentData', JSON.stringify(commentData));
    
    // Add photos if any
    photos.forEach(photo => {
      formData.append('photos', photo);
    });
    
    const response = await fetch(`${API_BASE_URL}/comments-multipart`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create comment with photos');
    }
    
    const data = await response.json();
    return data.comment;
  } catch (error) {
    console.error('Error creating comment with photos:', error);
    throw error;
  }
} 