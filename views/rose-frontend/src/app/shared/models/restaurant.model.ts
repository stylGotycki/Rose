export type RestaurantStatus = 'ACTIVE' | 'ARCHIVED';

export interface RestaurantReadDto {
  id: string;
  name: string;
  description?: string | null;
  taxId?: string | null;
  status: RestaurantStatus;
  createdAt: string;
  updatedAt: string;
}

export interface RestaurantCreateDto {
  name: string;
  description?: string | null;
  taxId?: string | null;
}

export interface RestaurantUpdateDto {
  name: string;
  description?: string | null;
  taxId?: string | null;
}