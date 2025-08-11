import React, { useState } from 'react';
import { Box, Typography, Modal, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import { Comment } from '../../types';
import { useTranslation } from 'react-i18next';

interface CommentImagesGalleryProps {
  comments?: Comment[];
  photos?: Array<{url: string, commentUsername: string}>;
  maxImages?: number;
}

const CommentImagesGallery: React.FC<CommentImagesGalleryProps> = ({ comments, photos, maxImages = 5 }) => {
  const { t } = useTranslation();
  const [openModal, setOpenModal] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  
  // Extract images either from provided photos or from comments
  const allImages = photos || (comments ? comments.reduce<Array<{ url: string, commentUsername: string }>>((acc, comment) => {
    if (comment.photos && comment.photos.length > 0) {
      const commentImages = comment.photos.map(photo => ({
        url: photo,
        commentUsername: comment.username
      }));
      return [...acc, ...commentImages];
    }
    return acc;
  }, []) : []);
  
  // Show only a limited number of images in a single row
  const displayImages = allImages.slice(0, maxImages);
  
  const handleOpenModal = (index: number) => {
    setSelectedImageIndex(index);
    setOpenModal(true);
  };
  
  const handleCloseModal = () => {
    setOpenModal(false);
  };
  
  const handlePrevImage = (e: React.MouseEvent) => {
    e.stopPropagation();
    setSelectedImageIndex((prevIndex) => 
      prevIndex === 0 ? allImages.length - 1 : prevIndex - 1
    );
  };
  
  const handleNextImage = (e: React.MouseEvent) => {
    e.stopPropagation();
    setSelectedImageIndex((prevIndex) => 
      prevIndex === allImages.length - 1 ? 0 : prevIndex + 1
    );
  };
  
  if (allImages.length === 0) {
    return null;
  }
  
  return (
    <Box sx={{ mb: 2 }}>
      <Typography variant="subtitle1" gutterBottom sx={{ fontWeight: 'medium', mb: 0.5 }}>
        {t('restaurant.customerPhotos')}
      </Typography>
      
      <Box 
        sx={{
          display: 'flex',
          gap: 1,
          overflowX: 'auto',
          pb: 1,
          '&::-webkit-scrollbar': {
            height: '6px',
          },
          '&::-webkit-scrollbar-track': {
            background: '#f1f1f1',
            borderRadius: '4px',
          },
          '&::-webkit-scrollbar-thumb': {
            background: '#c1c1c1',
            borderRadius: '4px',
          }
        }}
      >
        {displayImages.map((image, index) => (
          <Box 
            key={index}
            sx={{
              flex: '0 0 auto',
              height: { xs: '80px', sm: '100px', md: '120px' },
              width: 'auto',
              aspectRatio: '1',
              borderRadius: 1,
              overflow: 'hidden',
              cursor: 'pointer',
              '&:hover': {
                opacity: 0.9,
              }
            }}
            onClick={() => handleOpenModal(index)}
          >
            <Box
              component="img"
              src={image.url}
              alt={`Photo by ${image.commentUsername}`}
              sx={{
                height: '100%',
                width: '100%',
                objectFit: 'cover',
              }}
              loading="lazy"
            />
          </Box>
        ))}
        
        {allImages.length > maxImages && (
          <Box 
            sx={{
              flex: '0 0 auto',
              height: { xs: '80px', sm: '100px', md: '120px' },
              width: 'auto',
              aspectRatio: '1',
              borderRadius: 1,
              overflow: 'hidden',
              cursor: 'pointer',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              backgroundColor: 'rgba(0, 0, 0, 0.5)',
            }}
            onClick={() => handleOpenModal(maxImages)}
          >
            <Box
              sx={{
                color: 'white',
                fontWeight: 'bold',
                fontSize: '1rem',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                width: '100%',
                height: '100%',
              }}
            >
              +{allImages.length - maxImages}
            </Box>
          </Box>
        )}
      </Box>
      
      <Modal
        open={openModal}
        onClose={handleCloseModal}
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Box
          sx={{
            position: 'relative',
            maxWidth: '90vw',
            maxHeight: '90vh',
            outline: 'none',
          }}
          onClick={handleCloseModal}
        >
          <IconButton
            sx={{
              position: 'absolute',
              top: -40,
              right: 0,
              color: 'white',
              zIndex: 10,
            }}
            onClick={handleCloseModal}
          >
            <CloseIcon />
          </IconButton>
          
          <IconButton
            sx={{
              position: 'absolute',
              top: '50%',
              left: -50,
              transform: 'translateY(-50%)',
              color: 'white',
              zIndex: 10,
              backgroundColor: 'rgba(0, 0, 0, 0.3)',
              '&:hover': {
                backgroundColor: 'rgba(0, 0, 0, 0.6)',
              }
            }}
            onClick={handlePrevImage}
          >
            <ArrowBackIosNewIcon />
          </IconButton>
          
          <Box
            component="img"
            src={allImages[selectedImageIndex]?.url}
            alt={`Full size photo by ${allImages[selectedImageIndex]?.commentUsername}`}
            sx={{
              maxWidth: '90vw',
              maxHeight: '80vh',
              objectFit: 'contain',
              borderRadius: 1,
            }}
            onClick={(e) => e.stopPropagation()}
          />
          
          <IconButton
            sx={{
              position: 'absolute',
              top: '50%',
              right: -50,
              transform: 'translateY(-50%)',
              color: 'white',
              zIndex: 10,
              backgroundColor: 'rgba(0, 0, 0, 0.3)',
              '&:hover': {
                backgroundColor: 'rgba(0, 0, 0, 0.6)',
              }
            }}
            onClick={handleNextImage}
          >
            <ArrowForwardIosIcon />
          </IconButton>
          
          <Typography
            sx={{
              position: 'absolute',
              bottom: -40,
              left: 0,
              color: 'white',
              fontSize: '0.875rem',
            }}
          >
            {t('restaurant.photoBy', { username: allImages[selectedImageIndex]?.commentUsername })}
          </Typography>
          
          <Typography
            sx={{
              position: 'absolute',
              bottom: -40,
              right: 0,
              color: 'white',
              fontSize: '0.875rem',
            }}
          >
            {selectedImageIndex + 1} / {allImages.length}
          </Typography>
        </Box>
      </Modal>
    </Box>
  );
};

export default CommentImagesGallery; 