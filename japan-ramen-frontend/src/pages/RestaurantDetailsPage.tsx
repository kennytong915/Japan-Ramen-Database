import React, { useState, useEffect, useRef } from 'react';
import { useParams, Link as RouterLink } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  CircularProgress, 
  Card, 
  CardContent, 
  Button, 
  Chip,
  Divider,
  Rating,
  Paper,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  TextField,
  Alert,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Link,
  IconButton,
  Pagination
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { getRestaurantById, getCommentsByRestaurant, createComment, updateComment, deleteComment, createCommentWithPhotos, getPaginatedCommentsByRestaurant, getAllPhotosFromRestaurant } from '../services/api';
import { Restaurant, Comment, CommentRequest, GenreObject, SoupBaseObject, SoupBase, PageResponse } from '../types';
import CommentCard from '../components/UI/CommentCard';
import RamenDiningIcon from '@mui/icons-material/RamenDining';
import PlaceIcon from '@mui/icons-material/Place';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ChairIcon from '@mui/icons-material/Chair';
import SoupKitchenIcon from '@mui/icons-material/SoupKitchen';
import CategoryIcon from '@mui/icons-material/Category';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import InfoIcon from '@mui/icons-material/Info';
import CommentIcon from '@mui/icons-material/Comment';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import LaunchIcon from '@mui/icons-material/Launch';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import InstagramIcon from '@mui/icons-material/Instagram';
import TwitterIcon from '@mui/icons-material/Twitter';
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';
import PhotoIcon from '@mui/icons-material/Photo';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import axios from 'axios';
import { FileUploadResponse } from '../types';
import CommentImagesGallery from '../components/UI/CommentImagesGallery';

// Extended types to match the actual API response
interface ExtendedQueueMethod {
  id?: number;
  name: string;
  description?: string;
  chineseLabel?: string;
  type?: string;
  detailedGuide?: string;
}

interface ExtendedAddress {
  id?: number;
  areaId?: number;
  area?: Area;
  street?: string;
  building?: string;
  postCode?: string;
  lat?: number;
  lng?: number;
  fullAddress?: string;
  detailedAddress?: string;
  fullAddressInEnglish?: string;
  floor?: string | null;
  unit?: string | null;
  postalCode?: string;
}

interface Area {
  id: number;
  prefectureId: number;
  prefecture?: Prefecture;
  name: string;
  nameInEnglish?: string;
  fullName?: string;
  fullNameInEnglish?: string;
}

interface Prefecture {
  id: number;
  name: string;
}

// Extended restaurant interface to match our API response
interface ExtendedRestaurant extends Omit<Restaurant, 'queueMethod' | 'address'> {
  queueMethod?: ExtendedQueueMethod;
  address?: ExtendedAddress;
}

const RestaurantDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { isAuthenticated, user } = useAuth();
  
  const [restaurant, setRestaurant] = useState<ExtendedRestaurant | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Pagination state
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalComments, setTotalComments] = useState(0);
  const [pageSize] = useState(5);
  
  // Comment form state
  const [isCommentFormOpen, setIsCommentFormOpen] = useState(false);
  const [commentLoading, setCommentLoading] = useState(false);
  const [commentError, setCommentError] = useState<string | null>(null);
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  
  // Comment form fields
  const [foodComment, setFoodComment] = useState('');
  const [environmentComment, setEnvironmentComment] = useState('');
  const [visitingComment, setVisitingComment] = useState('');
  const [foodScore, setFoodScore] = useState<number | null>(null);
  const [environmentScore, setEnvironmentScore] = useState<number | null>(null);
  const [visitingScore, setVisitingScore] = useState<number | null>(null);
  
  // Delete dialog
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [commentToDelete, setCommentToDelete] = useState<number | null>(null);
  
  // Photo upload state
  const [photos, setPhotos] = useState<File[]>([]);
  const [uploadingPhotos, setUploadingPhotos] = useState(false);
  const [photoError, setPhotoError] = useState<string | null>(null);
  const [photoPreviewUrls, setPhotoPreviewUrls] = useState<string[]>([]);
  const [existingPhotos, setExistingPhotos] = useState<string[]>([]);
  const fileInputRef = React.useRef<HTMLInputElement>(null);
  
  // Reference to comments section for scrolling
  const commentsRef = useRef<HTMLDivElement>(null);
  
  // New state for gallery photos
  const [galleryPhotos, setGalleryPhotos] = useState<Array<{url: string, commentUsername: string}>>([]);
  const [loadingPhotos, setLoadingPhotos] = useState(false);
  
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const restaurantData = await getRestaurantById(Number(id));
        setRestaurant(restaurantData);
        
        // Use paginated comments instead of getting all at once
        const commentsData = await getPaginatedCommentsByRestaurant(Number(id), page, pageSize);
        setComments(commentsData.content);
        setTotalPages(commentsData.totalPages);
        setTotalComments(commentsData.totalElements);
        
        setError(null);
      } catch (err) {
        console.error('Error loading restaurant data:', err);
        setError(t('error.loadingRestaurant'));
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      loadData();
    }
  }, [id, t, page, pageSize]);
  
  // New useEffect to load all photos for the gallery
  useEffect(() => {
    const loadAllPhotos = async () => {
      if (!id) return;
      
      try {
        setLoadingPhotos(true);
        const photosData = await getAllPhotosFromRestaurant(Number(id));
        setGalleryPhotos(photosData);
      } catch (err) {
        console.error('Error loading photos:', err);
        // Don't set error state here since it's not critical
      } finally {
        setLoadingPhotos(false);
      }
    };
    
    loadAllPhotos();
  }, [id]);
  
  // Refresh gallery photos when a comment is added, edited, or deleted
  useEffect(() => {
    if (id && (commentLoading === false)) {
      const refreshPhotos = async () => {
        try {
          const photosData = await getAllPhotosFromRestaurant(Number(id));
          setGalleryPhotos(photosData);
        } catch (err) {
          console.error('Error refreshing photos:', err);
        }
      };
      
      refreshPhotos();
    }
  }, [id, commentLoading]);
  
  const handleOpenCommentForm = () => {
    setIsCommentFormOpen(true);
    setEditingCommentId(null);
    resetCommentForm();
    resetPhotoState();
    
    // Scroll to comments section
    if (commentsRef.current) {
      commentsRef.current.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  };
  
  const handleCloseCommentForm = () => {
    setIsCommentFormOpen(false);
    setEditingCommentId(null);
    resetCommentForm();
    resetPhotoState();
  };
  
  const resetCommentForm = () => {
    setFoodComment('');
    setEnvironmentComment('');
    setVisitingComment('');
    setFoodScore(null);
    setEnvironmentScore(null);
    setVisitingScore(null);
    setCommentError(null);
  };
  
  const resetPhotoState = () => {
    setPhotos([]);
    setPhotoPreviewUrls([]);
    setPhotoError(null);
    setExistingPhotos([]);
  };
  
  const handleEditComment = (commentId: number) => {
    const comment = comments.find(c => c.id === commentId);
    if (comment) {
      setEditingCommentId(commentId);
      setFoodComment(comment.foodComment || '');
      setEnvironmentComment(comment.environmentComment || '');
      setVisitingComment(comment.visitingComment || '');
      setFoodScore(comment.foodScore || null);
      setEnvironmentScore(comment.environmentScore || null);
      setVisitingScore(comment.visitingScore || null);
      
      // Set existing photos if any
      if (comment.photos && comment.photos.length > 0) {
        setExistingPhotos(comment.photos);
      } else {
        setExistingPhotos([]);
      }
      
      setIsCommentFormOpen(true);
    }
  };
  
  const handleOpenDeleteDialog = (commentId: number) => {
    setCommentToDelete(commentId);
    setDeleteDialogOpen(true);
  };
  
  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setCommentToDelete(null);
  };
  
  const handleDeleteComment = async () => {
    if (commentToDelete) {
      try {
        await deleteComment(commentToDelete);
        
        // Refresh comments after deletion
        const commentsData = await getPaginatedCommentsByRestaurant(Number(id), page, pageSize);
        setComments(commentsData.content);
        setTotalPages(commentsData.totalPages);
        setTotalComments(commentsData.totalElements);
        
        handleCloseDeleteDialog();
      } catch (err) {
        console.error('Error deleting comment:', err);
        setCommentError(t('error.commentDelete'));
      }
    }
  };
  
  const handlePhotoSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (!files || files.length === 0) return;
    
    const selectedFiles = Array.from(files);
    
    // Check file types
    const invalidFiles = selectedFiles.filter(
      file => !['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
    );
    
    if (invalidFiles.length > 0) {
      setPhotoError(`Invalid file type(s). Only JPG and PNG are allowed.`);
      return;
    }
    
    // Check if adding new photos exceeds the limit
    const totalPhotos = photos.length + selectedFiles.length + existingPhotos.length;
    if (totalPhotos > 7) {
      setPhotoError(`Maximum 7 photos allowed.`);
      return;
    }
    
    setPhotos([...photos, ...selectedFiles]);
    
    // Create preview URLs
    const newPreviewUrls = selectedFiles.map(file => URL.createObjectURL(file));
    setPhotoPreviewUrls([...photoPreviewUrls, ...newPreviewUrls]);
    
    setPhotoError(null);
  };
  
  const handleRemovePhoto = (index: number) => {
    const newPhotos = [...photos];
    const newPreviewUrls = [...photoPreviewUrls];
    
    // Release object URL to avoid memory leaks
    URL.revokeObjectURL(newPreviewUrls[index]);
    
    newPhotos.splice(index, 1);
    newPreviewUrls.splice(index, 1);
    
    setPhotos(newPhotos);
    setPhotoPreviewUrls(newPreviewUrls);
    
    if (photoError && newPhotos.length < 7) {
      setPhotoError(null);
    }
  };
  
  const handleRemoveExistingPhoto = (index: number) => {
    const newExistingPhotos = [...existingPhotos];
    newExistingPhotos.splice(index, 1);
    setExistingPhotos(newExistingPhotos);
    
    if (photoError && newExistingPhotos.length + photos.length < 7) {
      setPhotoError(null);
    }
  };
  
  const handleUploadPhotos = async (commentId: number): Promise<boolean> => {
    if (photos.length === 0) {
      return true; // No photos to upload, return success
    }
    
    setUploadingPhotos(true);
    
    try {
      const formData = new FormData();
      photos.forEach(photo => {
        formData.append('files', photo);
      });
      
      const token = localStorage.getItem('token');
      const response = await axios.post<FileUploadResponse>(
        `http://localhost:8080/api/photos/comment/${commentId}`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
          }
        }
      );
      
      if (response.data.errors) {
        const errorMessages = Object.values(response.data.errors).join(', ');
        setPhotoError(`Some photos couldn't be uploaded: ${errorMessages}`);
        return false;
      }
      
      return true;
    } catch (err) {
      console.error('Error uploading photos:', err);
      setPhotoError('Failed to upload photos');
      return false;
    } finally {
      setUploadingPhotos(false);
    }
  };
  
  const handleSubmitComment = async () => {
    // Validate form
    if (!foodScore || !environmentScore || !visitingScore) {
      setCommentError(t('comments.allScoresRequired'));
      return;
    }
    
    setCommentLoading(true);
    setCommentError(null);
    
    try {
      // Calculate overall score (average of all scores)
      const overallScore = Math.round(((foodScore + environmentScore + visitingScore) / 3) * 10) / 10;
      
      const commentData: CommentRequest = {
        restaurantId: Number(id),
        foodComment,
        environmentComment,
        visitingComment,
        foodScore,
        environmentScore,
        visitingScore,
        overallScore
      };
      
      if (editingCommentId) {
        // For editing comments, we still use the separate steps since our endpoint
        // only supports new comment creation with photos
        const updatedComment = await updateComment(editingCommentId, commentData);
        
        // Upload photos if any are selected
        if (photos.length > 0) {
          const uploadSuccess = await handleUploadPhotos(editingCommentId);
          if (!uploadSuccess) {
            // If photos failed to upload, we still updated the comment text
            // We'll keep the dialog open so they can retry
            setCommentLoading(false);
            return;
          }
        }
        
        // Refresh the comments to get updated data including photo URLs
        const commentsData = await getPaginatedCommentsByRestaurant(Number(id), page, pageSize);
        setComments(commentsData.content);
        setTotalPages(commentsData.totalPages);
        setTotalComments(commentsData.totalElements);
      } else {
        // For new comments, use the combined endpoint
        if (photos.length > 0) {
          // If we have photos, use the new multipart endpoint
          const newComment = await createCommentWithPhotos(commentData, photos);
          
          // Refresh comments to include the new one with photos
          const commentsData = await getPaginatedCommentsByRestaurant(Number(id), page, pageSize);
          setComments(commentsData.content);
          setTotalPages(commentsData.totalPages);
          setTotalComments(commentsData.totalElements);
        } else {
          // If no photos, use the original endpoint
          const newComment = await createComment(commentData);
          
          // Add the new comment to the list
          // Note: Since we're using pagination now, we need to refresh from the API
          const commentsData = await getPaginatedCommentsByRestaurant(Number(id), page, pageSize);
          setComments(commentsData.content);
          setTotalPages(commentsData.totalPages);
          setTotalComments(commentsData.totalElements);
        }
      }
      
      handleCloseCommentForm();
    } catch (err) {
      console.error('Error submitting comment:', err);
      setCommentError(t('error.commentSubmit'));
    } finally {
      setCommentLoading(false);
    }
  };
  
  // Handle page change with scrolling
  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value - 1); // Convert 1-based page to 0-based for API
    
    // Scroll to comments section after data loads
    setTimeout(() => {
      if (commentsRef.current) {
        commentsRef.current.scrollIntoView({ 
          behavior: 'smooth',
          block: 'start'
        });
      }
    }, 100);
  };
  
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '50vh' }}>
        <CircularProgress />
      </Box>
    );
  }
  
  if (error || !restaurant) {
    return (
      <Card sx={{ p: 2, backgroundColor: 'error.dark' }}>
        <CardContent>
          <Typography color="error.contrastText">{error || t('error.restaurantNotFound')}</Typography>
          <Button
            component={RouterLink}
            to="/"
            startIcon={<ArrowBackIcon />}
            sx={{ mt: 2 }}
          >
            {t('error.returnHome')}
          </Button>
        </CardContent>
      </Card>
    );
  }
  
  // Format date if available
  const formatOpeningDate = (dateString?: string) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  return (
    <Box sx={{ 
      px: { xs: 2, sm: 3, md: 4 },
      mx: { xs: 0, md: 'auto' },
      width: { xs: '100%', md: '60vw' },
      maxWidth: '1200px',
      position: 'relative'
    }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Button
          component={RouterLink}
          to="/ranking"
          startIcon={<ArrowBackIcon />}
          sx={{ mb: 3 }}
        >
          {t('ranking.title')}
        </Button>
        
        <Paper sx={{ p: 4, mb: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
            <Box>
              <Typography variant="h4" component="h1" fontWeight="bold">
                {restaurant.name}
              </Typography>
              
              {restaurant.address && restaurant.address.area && (
                <Typography variant="subtitle1" color="text.secondary">
                  {restaurant.address.area.fullName || `${restaurant.address.area.prefecture?.name || ''}, ${restaurant.address.area.name || ''}`}
                </Typography>
              )}
              
              {restaurant.score && (
                <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                  <Typography variant="body1" mr={1} fontWeight="bold">
                    {t('restaurant.score')}:
                  </Typography>
                  <Rating value={restaurant.score / 2} precision={0.5} readOnly />
                  <Typography variant="body1" ml={1} fontWeight="bold">
                    {restaurant.score.toFixed(1)}
                  </Typography>
                </Box>
              )}
            </Box>
            
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
              {/* Empty box for layout */}
            </Box>
          </Box>
          
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 3 }}>
            {restaurant.genres && restaurant.genres.map((genre: GenreObject) => (
              <Chip 
                key={genre.name} 
                label={genre.chineseLabel} 
                color="primary"
                variant="outlined"
              />
            ))}
            
            {restaurant.soupBases && restaurant.soupBases.map((base, index) => (
              <Chip 
                key={index}
                label={typeof base === 'object' ? base.chineseLabel : base} 
                color="secondary"
                variant="outlined"
              />
            ))}
            
            {restaurant.reservationSystem && (
              <Chip 
                label={t('restaurant.reservationRequired')} 
                color="error"
              />
            )}
          </Box>
          
          {/* Comment Images Gallery - now uses galleryPhotos instead of comments */}
          {!loadingPhotos && galleryPhotos.length > 0 && (
            <Box sx={{ mt: 2, mb: 2 }}>
              <CommentImagesGallery photos={galleryPhotos} maxImages={5} />
            </Box>
          )}
          
          <Divider sx={{ my: 2 }} />
          
          <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 4 }}>
            <Box sx={{ width: { xs: '100%', md: '50%' } }}>
              <List>
                {restaurant.address && (
                  <ListItem>
                    <ListItemIcon>
                      <PlaceIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.address')}
                      secondary={restaurant.address.fullAddress || 
                        `${restaurant.address.area?.prefecture?.name || ''} ${restaurant.address.area?.name || ''} ${restaurant.address.detailedAddress || ''} ${restaurant.address.building || ''}`}
                    />
                  </ListItem>
                )}
                
                {restaurant.openingHours && (
                  <ListItem>
                    <ListItemIcon>
                      <AccessTimeIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.openingHours')}
                      secondary={restaurant.openingHours}
                    />
                  </ListItem>
                )}
                
                {restaurant.restDay && (
                  <ListItem>
                    <ListItemIcon>
                      <AccessTimeIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.restDay')}
                      secondary={restaurant.restDay}
                    />
                  </ListItem>
                )}
                
                {restaurant.seats && (
                  <ListItem>
                    <ListItemIcon>
                      <ChairIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.seats')}
                      secondary={restaurant.seats}
                    />
                  </ListItem>
                )}
                
                {restaurant.openingDate && (
                  <ListItem>
                    <ListItemIcon>
                      <CalendarMonthIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.openingDate')}
                      secondary={formatOpeningDate(restaurant.openingDate)}
                    />
                  </ListItem>
                )}
              </List>
            </Box>
            
            <Box sx={{ width: { xs: '100%', md: '50%' } }}>
              <List>
                {restaurant.queueMethod && (
                  <ListItem>
                    <ListItemIcon>
                      <SoupKitchenIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.queueMethod')}
                      secondary={restaurant.queueMethod.chineseLabel || restaurant.queueMethod.name}
                    />
                  </ListItem>
                )}
                
                {restaurant.socialMediaLinks && Object.keys(restaurant.socialMediaLinks).length > 0 && (
                  <ListItem>
                    <ListItemIcon>
                      <InfoIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary={t('restaurant.socialMedia')}
                      secondary={
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mt: 1 }}>
                          {restaurant.socialMediaLinks.Instagram && (
                            <Link 
                              href={restaurant.socialMediaLinks.Instagram}
                              target="_blank"
                              rel="noopener noreferrer"
                              sx={{ display: 'flex', alignItems: 'center', gap: 1 }}
                            >
                              <InstagramIcon fontSize="small" />
                              Instagram
                            </Link>
                          )}
                          {restaurant.socialMediaLinks.Twitter && (
                            <Link 
                              href={restaurant.socialMediaLinks.Twitter}
                              target="_blank"
                              rel="noopener noreferrer"
                              sx={{ display: 'flex', alignItems: 'center', gap: 1 }}
                            >
                              <TwitterIcon fontSize="small" />
                              Twitter
                            </Link>
                          )}
                        </Box>
                      }
                    />
                  </ListItem>
                )}
              </List>
            </Box>
          </Box>
          
          {restaurant.description && (
            <>
              <Divider sx={{ my: 3 }} />
              <Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <InfoIcon sx={{ mr: 1, color: 'primary.main' }} />
                  <Typography variant="h6" fontWeight="bold">
                    {t('restaurant.description')}
                  </Typography>
                </Box>
                <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
                  {restaurant.description}
                </Typography>
              </Box>
            </>
          )}
          
          {restaurant.menuContent && (
            <>
              <Divider sx={{ my: 3 }} />
              <Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <MenuBookIcon sx={{ mr: 1, color: 'primary.main' }} />
                  <Typography variant="h6" fontWeight="bold">
                    {t('restaurant.menu')}
                  </Typography>
                </Box>
                <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
                  {restaurant.menuContent}
                </Typography>
              </Box>
            </>
          )}
          
          {restaurant.queueMethod && restaurant.queueMethod.detailedGuide && (
            <>
              <Divider sx={{ my: 3 }} />
              <Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <SoupKitchenIcon sx={{ mr: 1, color: 'primary.main' }} />
                  <Typography variant="h6" fontWeight="bold">
                    {t('restaurant.queueGuide')}
                  </Typography>
                </Box>
                <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
                  {restaurant.queueMethod.detailedGuide}
                </Typography>
              </Box>
            </>
          )}
        </Paper>
        
        {/* Comments Section */}
        <Paper sx={{ p: { xs: 2, sm: 3, md: 4 }, mt: 3 }} ref={commentsRef} id="comments-section">
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <CommentIcon sx={{ mr: 1, color: 'primary.main' }} />
              <Typography variant="h5" fontWeight="bold">
                {t('comments.title')} ({totalComments})
              </Typography>
            </Box>
            
            {isAuthenticated && (
              <Button
                variant="contained"
                onClick={handleOpenCommentForm}
                startIcon={<RamenDiningIcon />}
              >
                {t('comments.writeComment')}
              </Button>
            )}
          </Box>
          
          {comments.length === 0 ? (
            <Box sx={{ p: 4, textAlign: 'center' }}>
              <Typography>{t('comments.noComments')}</Typography>
            </Box>
          ) : (
            <>
              <Box sx={{ 
                display: 'flex', 
                flexDirection: 'column', 
                gap: { xs: 2, sm: 3 } 
              }}>
                {comments.map((comment, index) => (
                  <CommentCard
                    key={comment.id}
                    comment={comment}
                    delay={index}
                    onEdit={handleEditComment}
                    onDelete={handleOpenDeleteDialog}
                  />
                ))}
              </Box>
              
              {/* Pagination */}
              {totalPages > 1 && (
                <Box sx={{ mt: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {t('ranking.showing')} {page * pageSize + 1} - {Math.min(page * pageSize + comments.length, totalComments)}，
                    {t('ranking.of')} {totalComments} {t('ranking.comments')}
                  </Typography>
                  <Pagination 
                    count={totalPages}
                    page={page + 1}
                    onChange={handlePageChange}
                    color="primary"
                    size="large"
                  />
                </Box>
              )}
            </>
          )}
        </Paper>
        
        {/* Comment Form Dialog */}
        <Dialog open={isCommentFormOpen} onClose={handleCloseCommentForm} maxWidth="md" fullWidth>
          <DialogTitle>
            {editingCommentId ? t('comments.editComment') : t('comments.writeComment')}
          </DialogTitle>
          <DialogContent>
            {commentError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {commentError}
              </Alert>
            )}
            
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                {t('comments.foodScore')}
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Rating
                  value={foodScore}
                  onChange={(event, newValue) => {
                    setFoodScore(newValue);
                  }}
                  precision={1}
                />
                <Typography variant="body2" sx={{ ml: 2 }}>
                  {foodScore ? `${foodScore}/5` : ''}
                </Typography>
              </Box>
              <TextField
                label={t('comments.foodComment')}
                fullWidth
                multiline
                rows={3}
                value={foodComment}
                onChange={(e) => setFoodComment(e.target.value)}
                variant="outlined"
                margin="normal"
              />
            </Box>
            
            <Box sx={{ mt: 3 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                {t('comments.environmentScore')}
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Rating
                  value={environmentScore}
                  onChange={(event, newValue) => {
                    setEnvironmentScore(newValue);
                  }}
                  precision={1}
                />
                <Typography variant="body2" sx={{ ml: 2 }}>
                  {environmentScore ? `${environmentScore}/5` : ''}
                </Typography>
              </Box>
              <TextField
                label={t('comments.environmentComment')}
                fullWidth
                multiline
                rows={3}
                value={environmentComment}
                onChange={(e) => setEnvironmentComment(e.target.value)}
                variant="outlined"
                margin="normal"
              />
            </Box>
            
            <Box sx={{ mt: 3 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                {t('comments.visitingScore')}
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Rating
                  value={visitingScore}
                  onChange={(event, newValue) => {
                    setVisitingScore(newValue);
                  }}
                  precision={1}
                />
                <Typography variant="body2" sx={{ ml: 2 }}>
                  {visitingScore ? `${visitingScore}/5` : ''}
                </Typography>
              </Box>
              <TextField
                label={t('comments.visitingComment')}
                fullWidth
                multiline
                rows={3}
                value={visitingComment}
                onChange={(e) => setVisitingComment(e.target.value)}
                variant="outlined"
                margin="normal"
              />
            </Box>
            
            {/* Photo upload section */}
            <Box sx={{ mt: 3 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                {t('comments.photos')} ({existingPhotos.length + photos.length}/7)
              </Typography>
              
              {photoError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {photoError}
                </Alert>
              )}
              
              {/* Existing photos */}
              {existingPhotos.length > 0 && (
                <Box sx={{ mb: 2 }}>
                  <Typography variant="subtitle2" gutterBottom>
                    {t('comments.existingPhotos')}
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {existingPhotos.map((photoUrl, index) => (
                      <Box
                        key={index}
                        sx={{
                          position: 'relative',
                          width: 100,
                          height: 100,
                          border: '1px solid #ccc',
                          borderRadius: 1,
                          overflow: 'hidden',
                        }}
                      >
                        <img
                          src={photoUrl}
                          alt={`Existing photo ${index + 1}`}
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                        <IconButton
                          size="small"
                          sx={{
                            position: 'absolute',
                            top: 0,
                            right: 0,
                            backgroundColor: 'rgba(0, 0, 0, 0.5)',
                            color: 'white',
                            '&:hover': { backgroundColor: 'rgba(255, 0, 0, 0.7)' },
                          }}
                          onClick={() => handleRemoveExistingPhoto(index)}
                        >
                          <DeleteForeverIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    ))}
                  </Box>
                </Box>
              )}
              
              {/* New photos preview */}
              {photoPreviewUrls.length > 0 && (
                <Box sx={{ mb: 2 }}>
                  <Typography variant="subtitle2" gutterBottom>
                    {t('comments.newPhotos')}
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {photoPreviewUrls.map((previewUrl, index) => (
                      <Box
                        key={index}
                        sx={{
                          position: 'relative',
                          width: 100,
                          height: 100,
                          border: '1px solid #ccc',
                          borderRadius: 1,
                          overflow: 'hidden',
                        }}
                      >
                        <img
                          src={previewUrl}
                          alt={`Preview ${index + 1}`}
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                        <IconButton
                          size="small"
                          sx={{
                            position: 'absolute',
                            top: 0,
                            right: 0,
                            backgroundColor: 'rgba(0, 0, 0, 0.5)',
                            color: 'white',
                            '&:hover': { backgroundColor: 'rgba(255, 0, 0, 0.7)' },
                          }}
                          onClick={() => handleRemovePhoto(index)}
                        >
                          <DeleteForeverIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    ))}
                  </Box>
                </Box>
              )}
              
              {/* Upload button */}
              {existingPhotos.length + photos.length < 7 && (
                <Button
                  variant="outlined"
                  component="label"
                  startIcon={<PhotoCameraIcon />}
                  disabled={uploadingPhotos}
                  sx={{ mt: 1 }}
                >
                  {t('comments.addPhotos')}
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/jpeg,image/png,image/jpg"
                    multiple
                    onChange={handlePhotoSelect}
                    hidden
                  />
                </Button>
              )}
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseCommentForm}>
              {t('comments.cancel')}
            </Button>
            <Button 
              onClick={handleSubmitComment} 
              color="primary" 
              variant="contained"
              disabled={commentLoading || uploadingPhotos}
              startIcon={commentLoading || uploadingPhotos ? <CircularProgress size={20} /> : null}
            >
              {commentLoading || uploadingPhotos ? t('comments.submitting') : t('comments.submitComment')}
            </Button>
          </DialogActions>
        </Dialog>
        
        {/* Delete Confirmation Dialog */}
        <Dialog
          open={deleteDialogOpen}
          onClose={handleCloseDeleteDialog}
          aria-labelledby="delete-dialog-title"
        >
          <DialogTitle id="delete-dialog-title">
            {t('comments.deleteComment')}
          </DialogTitle>
          <DialogContent>
            <DialogContentText>
              {t('comments.deleteConfirmation')}
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDeleteDialog} color="primary">
              {t('comments.cancel')}
            </Button>
            <Button onClick={handleDeleteComment} color="error">
              {t('comments.delete')}
            </Button>
          </DialogActions>
        </Dialog>
      </motion.div>
    </Box>
  );
};

export default RestaurantDetailsPage; 