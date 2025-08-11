import React from 'react';
import { Box, SxProps, Theme } from '@mui/material';

interface GridFixedItemProps {
  children: React.ReactNode;
  xs?: number;
  sm?: number;
  md?: number;
  lg?: number;
  xl?: number;
  sx?: SxProps<Theme>;
}

const GridFixedItem: React.FC<GridFixedItemProps> = ({
  children,
  xs,
  sm,
  md,
  lg,
  xl,
  sx
}) => {
  // Convert grid numbers to width percentages
  const getWidthValue = (value: number | undefined) => {
    if (value === undefined) return undefined;
    return `${(value / 12) * 100}%`;
  };

  return (
    <Box
      sx={{
        width: {
          xs: getWidthValue(xs),
          sm: getWidthValue(sm),
          md: getWidthValue(md),
          lg: getWidthValue(lg),
          xl: getWidthValue(xl)
        },
        ...sx
      }}
    >
      {children}
    </Box>
  );
};

export default GridFixedItem; 