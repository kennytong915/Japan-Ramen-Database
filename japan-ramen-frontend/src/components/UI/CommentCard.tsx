import React, { useState } from 'react';
import { Card, CardContent, Typography, Box, Avatar, Rating, Divider, IconButton, Tooltip, ImageList, ImageListItem, Modal, Paper } from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { Comment } from '../../types';
import { useAuth } from '../../contexts/AuthContext';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import PlaceIcon from '@mui/icons-material/Place';
import StarIcon from '@mui/icons-material/Star';
import CloseIcon from '@mui/icons-material/Close';
import { format } from 'date-fns';

interface CommentCardProps {
  comment: Comment;
  delay?: number;
  onEdit?: (commentId: number) => void;
  onDelete?: (commentId: number) => void;
}

const CommentCard: React.FC<CommentCardProps> = ({ comment, delay = 0, onEdit, onDelete }) => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const isAuthor = user && user.id === comment.userId;
  
  const [openPhotoModal, setOpenPhotoModal] = useState(false);
  const [selectedPhotoIndex, setSelectedPhotoIndex] = useState(0);
  
  // Convert string date to formatted date
  const formattedDate = comment.updatedAt 
    ? format(new Date(comment.updatedAt), 'yyyy-MM-dd HH:mm')
    : format(new Date(comment.createdAt), 'yyyy-MM-dd HH:mm');
  
  // Whether it was edited
  const isEdited = comment.updatedAt && comment.updatedAt !== comment.createdAt;

  const handleOpenPhotoModal = (index: number) => {
    setSelectedPhotoIndex(index);
    setOpenPhotoModal(true);
  };

  const handleClosePhotoModal = () => {
    setOpenPhotoModal(false);
  };

  const handlePrevPhoto = () => {
    if (comment.photos && comment.photos.length > 0) {
      setSelectedPhotoIndex((prevIndex) => 
        prevIndex === 0 ? comment.photos!.length - 1 : prevIndex - 1
      );
    }
  };

  const handleNextPhoto = () => {
    if (comment.photos && comment.photos.length > 0) {
      setSelectedPhotoIndex((prevIndex) => 
        prevIndex === comment.photos!.length - 1 ? 0 : prevIndex + 1
      );
    }
  };

  return (
    <Paper
      elevation={0}
      component={motion.div}
      initial={{ opacity: 0, y: 50 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.1 * (delay || 0) }}
      sx={{
        p: { xs: 2, sm: 3 },
        mb: 2,
        borderRadius: 2,
        position: 'relative',
        overflow: 'visible',
        backgroundColor: 'background.paper',
      }}
    >
      <Card sx={{ 
        boxShadow: 'none', 
        background: 'transparent'
      }}>
        <CardContent sx={{ 
          p: { xs: 0.5, sm: 1 },
          '&:last-child': { pb: { xs: 0.5, sm: 1 } } 
        }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Avatar sx={{ bgcolor: 'primary.main', mr: 2 }}>
                {comment.username.charAt(0).toUpperCase()}
              </Avatar>
              <Box>
                <Typography variant="subtitle1" fontWeight="bold">
                  {comment.username}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {formattedDate} {isEdited && `(${t('comments.edited')})`}
                </Typography>
              </Box>
            </Box>
            
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mr: 2 }}>
                <Rating 
                  value={comment.averageScore / 2} 
                  precision={0.5} 
                  readOnly 
                  size="small"
                />
                <Typography variant="body2" sx={{ ml: 1 }}>
                  {comment.averageScore.toFixed(1)}
                </Typography>
              </Box>
              
              {isAuthor && (
                <Box>
                  {onEdit && (
                    <Tooltip title={t('comments.editComment')}>
                      <IconButton size="small" onClick={() => onEdit(comment.id)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  )}
                  {onDelete && (
                    <Tooltip title={t('comments.deleteComment')}>
                      <IconButton size="small" onClick={() => onDelete(comment.id)}>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  )}
                </Box>
              )}
            </Box>
          </Box>
          
          <Divider sx={{ my: 2 }} />
          
          <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <FastfoodIcon fontSize="small" sx={{ mr: 0.5, color: 'primary.main' }} />
              <Typography variant="subtitle2" fontWeight="bold">
                {t('comments.foodScore')}: {comment.foodScore}/5
              </Typography>
            </Box>
            {comment.foodComment && (
              <Typography variant="body2" sx={{ ml: 3 }}>
                {comment.foodComment}
              </Typography>
            )}
          </Box>
          
          <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <PlaceIcon fontSize="small" sx={{ mr: 0.5, color: 'primary.main' }} />
              <Typography variant="subtitle2" fontWeight="bold">
                {t('comments.environmentScore')}: {comment.environmentScore}/5
              </Typography>
            </Box>
            {comment.environmentComment && (
              <Typography variant="body2" sx={{ ml: 3 }}>
                {comment.environmentComment}
              </Typography>
            )}
          </Box>
          
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <StarIcon fontSize="small" sx={{ mr: 0.5, color: 'primary.main' }} />
              <Typography variant="subtitle2" fontWeight="bold">
                {t('comments.visitingScore')}: {comment.visitingScore}/5
              </Typography>
            </Box>
            {comment.visitingComment && (
              <Typography variant="body2" sx={{ ml: 3 }}>
                {comment.visitingComment}
              </Typography>
            )}
          </Box>
          
          {comment.overallScore && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="body1" fontWeight="bold">
                {t('comments.overallScore')}: {comment.overallScore}/5
              </Typography>
            </Box>
          )}

          {/* Photo gallery */}
          {comment.photos && comment.photos.length > 0 && (
            <Box sx={{ mt: 3 }}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                {t('comments.photos')} ({comment.photos.length})
              </Typography>
              <Box 
                sx={{ 
                  display: 'grid',
                  gridTemplateColumns: {
                    xs: 'repeat(2, 1fr)', // 2 columns on mobile (phones)
                    sm: 'repeat(3, 1fr)', // 3 columns on small tablets
                    md: 'repeat(auto-fill, minmax(150px, 1fr))' // Desktop layout (responsive)
                  },
                  gap: 1,
                  width: '100%'
                }}
              >
                {comment.photos.slice(0, comment.photos.length > 4 ? 3 : 4).map((photo, index) => (
                  <Box 
                    key={index} 
                    sx={{ 
                      cursor: 'pointer',
                      borderRadius: 1,
                      overflow: 'hidden',
                      height: {
                        xs: 120, // Smaller height on mobile
                        sm: 150  // Original height on larger screens
                      },
                      position: 'relative',
                      '&:hover': { 
                        opacity: 0.8,
                        transition: 'opacity 0.2s'
                      }
                    }}
                    onClick={() => handleOpenPhotoModal(index)}
                  >
                    <img
                      src={photo}
                      alt={`${comment.username}'s photo ${index + 1}`}
                      style={{ 
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover'
                      }}
                      loading="lazy"
                    />
                  </Box>
                ))}
                
                {comment.photos.length > 4 && (
                  <Box 
                    sx={{ 
                      cursor: 'pointer',
                      borderRadius: 1,
                      overflow: 'hidden',
                      height: {
                        xs: 120, // Smaller height on mobile
                        sm: 150  // Original height on larger screens
                      },
                      position: 'relative',
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                      display: 'flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      '&:hover': { 
                        opacity: 0.8,
                        transition: 'opacity 0.2s'
                      }
                    }}
                    onClick={() => handleOpenPhotoModal(3)}
                  >
                    <Box
                      sx={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        opacity: 0.7,
                      }}
                    >
                      <img
                        src={comment.photos[3]}
                        alt={`${comment.username}'s photo 4`}
                        style={{ 
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover'
                        }}
                        loading="lazy"
                      />
                    </Box>
                    <Typography 
                      variant="h5" 
                      sx={{ 
                        color: 'white', 
                        zIndex: 2,
                        fontWeight: 'bold',
                        textShadow: '0px 0px 8px rgba(0,0,0,0.8)'
                      }}
                    >
                      +{comment.photos.length - 3}
                    </Typography>
                  </Box>
                )}
              </Box>
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Photo modal */}
      <Modal
        open={openPhotoModal}
        onClose={handleClosePhotoModal}
        aria-labelledby="photo-modal-title"
        aria-describedby="photo-modal-description"
      >
        <Box
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            bgcolor: 'background.paper',
            boxShadow: 24,
            p: 2,
            maxWidth: '90vw',
            maxHeight: '90vh',
            width: '100%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            borderRadius: 2,
            overflow: 'hidden'
          }}
        >
          <Box sx={{ 
            position: 'absolute', 
            top: 8, 
            right: 8, 
            zIndex: 10 
          }}>
            <IconButton 
              onClick={handleClosePhotoModal}
              sx={{ 
                bgcolor: 'rgba(0, 0, 0, 0.3)',
                color: 'white',
                '&:hover': {
                  bgcolor: 'rgba(0, 0, 0, 0.5)',
                }
              }}
            >
              <CloseIcon />
            </IconButton>
          </Box>
          
          <Typography variant="h6" sx={{ mb: 2, textAlign: 'center', width: '100%' }}>
            {t('comments.photoGallery')} - {selectedPhotoIndex + 1}/{comment.photos?.length || 0}
          </Typography>
          
          <Box
            sx={{
              position: 'relative',
              width: '100%',
              height: '70vh',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              overflow: 'hidden',
              backgroundColor: 'black'
            }}
          >
            {comment.photos && comment.photos.length > 0 && (
              <img
                src={comment.photos[selectedPhotoIndex]}
                alt={`${comment.username}'s photo ${selectedPhotoIndex + 1}`}
                style={{
                  maxWidth: '100%',
                  maxHeight: '100%',
                  objectFit: 'contain',
                  display: 'block'
                }}
              />
            )}
            
            {comment.photos && comment.photos.length > 1 && (
              <>
                <IconButton
                  sx={{
                    position: 'absolute',
                    left: 16,
                    backgroundColor: 'rgba(0, 0, 0, 0.3)',
                    color: 'white',
                    '&:hover': {
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    },
                    zIndex: 5
                  }}
                  onClick={handlePrevPhoto}
                  size="large"
                >
                  <Box sx={{ fontSize: '2rem' }}>&lt;</Box>
                </IconButton>
                
                <IconButton
                  sx={{
                    position: 'absolute',
                    right: 16,
                    backgroundColor: 'rgba(0, 0, 0, 0.3)',
                    color: 'white',
                    '&:hover': {
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    },
                    zIndex: 5
                  }}
                  onClick={handleNextPhoto}
                  size="large"
                >
                  <Box sx={{ fontSize: '2rem' }}>&gt;</Box>
                </IconButton>
              </>
            )}
          </Box>
        </Box>
      </Modal>
    </Paper>
  );
};

export default CommentCard; 