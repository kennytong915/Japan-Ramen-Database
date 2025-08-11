import React, { useState, useEffect } from 'react';
import { FormControl, InputLabel, Select, MenuItem, CircularProgress, Box, SelectChangeEvent } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { getAllPrefectures } from '../../services/api';
import { Prefecture } from '../../types';

interface PrefectureDropdownProps {
  value: number | '';
  onChange: (prefectureId: number | '') => void;
  fullWidth?: boolean;
}

const PrefectureDropdown: React.FC<PrefectureDropdownProps> = ({ value, onChange, fullWidth = true }) => {
  const { t } = useTranslation();
  const [prefectures, setPrefectures] = useState<Prefecture[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadPrefectures = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log('PrefectureDropdown: Loading prefectures...');
        const data = await getAllPrefectures();
        console.log('PrefectureDropdown: Prefectures loaded:', data);
        setPrefectures(data);
      } catch (err) {
        console.error('PrefectureDropdown: Error loading prefectures:', err);
        setError('Failed to load prefectures');
      } finally {
        setLoading(false);
      }
    };

    loadPrefectures();
  }, []);

  const handleChange = (event: SelectChangeEvent<unknown>) => {
    const value = event.target.value;
    console.log('PrefectureDropdown: Selection changed to:', value);
    onChange(value === '' ? '' : Number(value));
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <FormControl fullWidth={fullWidth} sx={{ mb: 0 }}>
      <InputLabel id="prefecture-dropdown-label">{t('ranking.prefecture')}</InputLabel>
      {loading ? (
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
          <CircularProgress size={20} sx={{ mr: 1 }} />
          {t('ranking.loading')}
        </Box>
      ) : (
        <Select
          labelId="prefecture-dropdown-label"
          value={value}
          label={t('ranking.prefecture')}
          onChange={handleChange}
        >
          <MenuItem value="">
            <em>{t('ranking.all')}</em>
          </MenuItem>
          {prefectures.map((prefecture) => (
            <MenuItem key={prefecture.id} value={prefecture.id}>
              {prefecture.name}
            </MenuItem>
          ))}
        </Select>
      )}
    </FormControl>
  );
};

export default PrefectureDropdown; 