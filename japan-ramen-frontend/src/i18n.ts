import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import zhTWTranslation from './locales/zh-TW/translation.json';

i18n
  .use(initReactI18next)
  .init({
    resources: {
      'zh-TW': {
        translation: zhTWTranslation,
      },
    },
    lng: 'zh-TW', // Default language
    fallbackLng: 'zh-TW',
    interpolation: {
      escapeValue: false, // React already safes from XSS
    },
  });

export default i18n; 