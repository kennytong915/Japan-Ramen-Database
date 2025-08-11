import React, { useState, useEffect } from 'react';
import { FormControl, InputLabel, Select, MenuItem, CircularProgress, Box, SelectChangeEvent, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { getAllGenres } from '../../services/api';

// Define the GenreObject interface to match the API response
interface GenreObject {
  name: string;
  chineseLabel: string;
}

interface GenreDropdownProps {
  value: string[];
  onChange: (genres: string[]) => void;
  fullWidth?: boolean;
}

const GenreDropdown: React.FC<GenreDropdownProps> = ({ value, onChange, fullWidth = true }) => {
  const { t } = useTranslation();
  const [genres, setGenres] = useState<GenreObject[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadGenres = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log('GenreDropdown: Loading genres...');
        const data = await getAllGenres();
        console.log('GenreDropdown: Genres loaded:', data);
        setGenres(data);
      } catch (err) {
        console.error('GenreDropdown: Error loading genres:', err);
        setError('Failed to load genres');
      } finally {
        setLoading(false);
      }
    };

    loadGenres();
  }, []);

  const handleChange = (event: SelectChangeEvent<string[]>) => {
    const selectedValues = event.target.value as string[];
    console.log('GenreDropdown: Selection changed to:', selectedValues);
    onChange(selectedValues);
  };

  // Find the genre object by name to get its chineseLabel
  const getGenreLabel = (genreName: string): string => {
    const genre = genres.find(g => g.name === genreName);
    return genre ? genre.chineseLabel : genreName;
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <FormControl fullWidth={fullWidth} sx={{ mb: 0 }}>
      <InputLabel id="genre-dropdown-label">{t('ranking.genre')}</InputLabel>
      {loading ? (
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
          <CircularProgress size={20} sx={{ mr: 1 }} />
          {t('ranking.loading')}
        </Box>
      ) : (
        <Select
          labelId="genre-dropdown-label"
          multiple
          value={value}
          label={t('ranking.genre')}
          onChange={handleChange}
          renderValue={(selected) => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {(selected as string[]).map((value) => (
                <Chip key={value} label={getGenreLabel(value)} size="small" color="primary" />
              ))}
            </Box>
          )}
        >
          {genres.map((genre) => (
            <MenuItem key={genre.name} value={genre.name}>
              {genre.chineseLabel}
            </MenuItem>
          ))}
        </Select>
      )}
    </FormControl>
  );
};

export default GenreDropdown; 