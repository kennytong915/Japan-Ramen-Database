import React, { useState, useEffect } from 'react';
import { FormControl, InputLabel, Select, MenuItem, CircularProgress, Box, SelectChangeEvent } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { getAllAreas } from '../../services/api';
import { Area } from '../../types';

interface AreaDropdownProps {
  value: number | '';
  onChange: (areaId: number | '') => void;
  prefectureId: number | null;
  fullWidth?: boolean;
}

const AreaDropdown: React.FC<AreaDropdownProps> = ({ value, onChange, prefectureId, fullWidth = true }) => {
  const { t } = useTranslation();
  const [areas, setAreas] = useState<Area[]>([]);
  const [filteredAreas, setFilteredAreas] = useState<Area[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Load all areas
  useEffect(() => {
    const loadAreas = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log('AreaDropdown: Loading areas...');
        const data = await getAllAreas();
        console.log('AreaDropdown: Areas loaded:', data);
        setAreas(data);
      } catch (err) {
        console.error('AreaDropdown: Error loading areas:', err);
        setError('Failed to load areas');
      } finally {
        setLoading(false);
      }
    };

    loadAreas();
  }, []);

  // Filter areas when prefecture changes
  useEffect(() => {
    console.log('AreaDropdown: Prefecture changed:', prefectureId);
    if (prefectureId) {
      const filtered = areas.filter(area => area.prefectureId === prefectureId);
      console.log('AreaDropdown: Filtered areas:', filtered);
      setFilteredAreas(filtered);
      
      // Reset the selected area if it doesn't belong to the selected prefecture
      if (value !== '' && !filtered.some(area => area.id === value)) {
        onChange('');
      }
    } else {
      setFilteredAreas([]);
      if (value !== '') {
        onChange('');
      }
    }
  }, [prefectureId, areas, value, onChange]);

  const handleChange = (event: SelectChangeEvent<unknown>) => {
    const selectedValue = event.target.value;
    console.log('AreaDropdown: Selection changed to:', selectedValue);
    onChange(selectedValue === '' ? '' : Number(selectedValue));
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <FormControl fullWidth={fullWidth} sx={{ mb: 0 }} disabled={!prefectureId}>
      <InputLabel id="area-dropdown-label">{t('ranking.area')}</InputLabel>
      {loading ? (
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
          <CircularProgress size={20} sx={{ mr: 1 }} />
          {t('ranking.loading')}
        </Box>
      ) : (
        <Select
          labelId="area-dropdown-label"
          value={value}
          label={t('ranking.area')}
          onChange={handleChange}
        >
          <MenuItem value="">
            <em>{t('ranking.all')}</em>
          </MenuItem>
          {filteredAreas.map((area) => (
            <MenuItem key={area.id} value={area.id}>
              {area.name}
            </MenuItem>
          ))}
        </Select>
      )}
    </FormControl>
  );
};

export default AreaDropdown; 