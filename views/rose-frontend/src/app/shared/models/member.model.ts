export type RestaurantMemberRole =
  | 'OWNER'
  | 'MANAGER'
  | 'CHEF'
  | 'WAITER'
  | 'STAFF';

export type RestaurantMemberStatus =
  | 'ACTIVE'
  | 'ARCHIVED';

export interface RestaurantMemberReadDto {
  id: string;
  restaurantId: string;
  userId: string;
  role: RestaurantMemberRole;
  status: RestaurantMemberStatus;
  createdAt: string;
  updatedAt: string;
}

export interface RestaurantMemberCreateDto {
  userId: string;
  role: RestaurantMemberRole;
}

export interface RestaurantMemberUpdateDto {
  role: RestaurantMemberRole;
}