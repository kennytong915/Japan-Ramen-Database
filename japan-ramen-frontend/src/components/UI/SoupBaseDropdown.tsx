import React, { useState, useEffect } from 'react';
import { FormControl, InputLabel, Select, MenuItem, CircularProgress, Box, SelectChangeEvent, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { getAllSoupBases } from '../../services/api';

interface SoupBaseDropdownProps {
  value: string[];
  onChange: (soupBases: string[]) => void;
  fullWidth?: boolean;
}

const SoupBaseDropdown: React.FC<SoupBaseDropdownProps> = ({ value, onChange, fullWidth = true }) => {
  const { t } = useTranslation();
  const [soupBases, setSoupBases] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadSoupBases = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log('SoupBaseDropdown: Loading soup bases...');
        const data = await getAllSoupBases();
        console.log('SoupBaseDropdown: Soup bases loaded:', data);
        setSoupBases(data);
      } catch (err) {
        console.error('SoupBaseDropdown: Error loading soup bases:', err);
        setError('Failed to load soup bases');
      } finally {
        setLoading(false);
      }
    };

    loadSoupBases();
  }, []);

  const handleChange = (event: SelectChangeEvent<string[]>) => {
    const selectedValues = event.target.value as string[];
    console.log('SoupBaseDropdown: Selection changed to:', selectedValues);
    onChange(selectedValues);
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <FormControl fullWidth={fullWidth} sx={{ mb: 0 }}>
      <InputLabel id="soup-base-dropdown-label">{t('ranking.soupBase')}</InputLabel>
      {loading ? (
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
          <CircularProgress size={20} sx={{ mr: 1 }} />
          {t('ranking.loading')}
        </Box>
      ) : (
        <Select
          labelId="soup-base-dropdown-label"
          multiple
          value={value}
          label={t('ranking.soupBase')}
          onChange={handleChange}
          renderValue={(selected) => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {(selected as string[]).map((value) => (
                <Chip key={value} label={value} size="small" color="secondary" />
              ))}
            </Box>
          )}
        >
          {soupBases.map((base) => (
            <MenuItem key={base} value={base}>
              {base}
            </MenuItem>
          ))}
        </Select>
      )}
    </FormControl>
  );
};

export default SoupBaseDropdown; 