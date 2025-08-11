// API response types
// Define GenreObject interface to match API response
export interface GenreObject {
  name: string;
  chineseLabel: string;
}

// Define SoupBaseObject interface to match API response
export interface SoupBaseObject {
  name: string;
  chineseLabel: string;
}

export interface Restaurant {
  id: number;
  name: string;
  score?: number;
  genres: GenreObject[];
  soupBases: SoupBase[] | SoupBaseObject[];
  reservationSystem?: boolean;
  queueMethod?: QueueMethod;
  address?: Address;
  seats?: number;
  socialMediaLinks?: Record<string, string>;
  menuContent?: string;
  openingHours?: string;
  restDay?: string;
  openingDate?: string;
  description?: string;
}

export interface RestaurantCard {
  id: number;
  name: string;
  prefectureName?: string;
  areaName?: string;
  score?: number;
}

export interface RestaurantRanking {
  id: number;
  name: string;
  score?: number;
  areaName?: string;
  prefectureName?: string;
  genres: GenreObject[];
  soupBases: SoupBase[] | SoupBaseObject[];
  thumbnailUrl?: string;
}

export interface Comment {
  id: number;
  userId: number;
  username: string;
  restaurantId: number;
  restaurantName: string;
  foodComment?: string;
  visitingComment?: string;
  environmentComment?: string;
  foodScore?: number;
  visitingScore?: number;
  environmentScore?: number;
  overallScore?: number;
  createdAt: string;
  updatedAt?: string;
  averageScore: number;
  photos?: string[];
}

export interface QueueMethod {
  id?: number;
  name: string;
  description?: string;
}

export interface Address {
  id?: number;
  areaId?: number;
  area?: Area;
  street?: string;
  building?: string;
  postCode?: string;
  lat?: number;
  lng?: number;
}

export interface Area {
  id: number;
  prefectureId: number;
  prefecture?: Prefecture;
  name: string;
  nameInEnglish?: string;
  fullName?: string;
  fullNameInEnglish?: string;
}

export interface Prefecture {
  id: number;
  name: string;
}

export enum Genre {
  TONKOTSU = "TONKOTSU",
  SHOYU = "SHOYU",
  MISO = "MISO",
  SHIO = "SHIO",
  TSUKEMEN = "TSUKEMEN",
  JIRO = "JIRO",
  TANTANMEN = "TANTANMEN",
  MAZESOBA = "MAZESOBA",
  OTHER = "OTHER"
}

export enum SoupBase {
  PORK = "PORK",
  CHICKEN = "CHICKEN",
  FISH = "FISH",
  VEGETABLE = "VEGETABLE",
  BEEF = "BEEF",
  OTHER = "OTHER"
}

// Auth Types
export interface User {
  id: number;
  username: string;
  role: string;
  createdAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  recaptchaResponse: string;
}

export interface RegistrationRequest {
  username: string;
  password: string;
  recaptchaResponse: string;
}

// Comment Creation/Update
export interface CommentRequest {
  restaurantId: number;
  foodComment?: string;
  visitingComment?: string;
  environmentComment?: string;
  foodScore?: number;
  visitingScore?: number;
  environmentScore?: number;
  overallScore: number;
}

// Filter Types for Rankings
export interface RankingFilter {
  prefectureId?: number;
  areaId?: number;
  genres?: Genre[];
  soupBases?: SoupBase[];
  minScore?: number;
  name?: string;
  reservationRequired?: boolean;
  sortBy?: string;
  sortDirection?: "ASC" | "DESC";
  page?: number;
  size?: number;
  paginated?: boolean;
}

// Pagination response
export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// File Upload Response
export interface FileUploadResponse {
  uploadedPhotos: string[];
  errors?: Record<string, string>;
} 