export type LocationStatus = 'ACTIVE' | 'ARCHIVED';

export interface LocationReadDto {
  id: string;
  restaurantId: string;
  name: string;
  city: string;
  street: string;
  buildingNumber: string;
  postalCode: string;
  status: LocationStatus;
  createdAt: string;
  updatedAt: string;
}

export interface LocationCreateDto {
  name: string;
  city: string;
  street: string;
  buildingNumber: string;
  postalCode: string;
}

export interface LocationUpdateDto {
  name: string;
  city: string;
  street: string;
  buildingNumber: string;
  postalCode: string;
}