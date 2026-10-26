export type EmployeeRole =
  | 'OWNER'
  | 'MANAGER'
  | 'CHEF'
  | 'WAITER'
  | 'STAFF';

export type EmployeeStatus =
  | 'ACTIVE'
  | 'ARCHIVED';

export interface EmployeeReadDto {
  id: string;
  restaurantId: string;
  userId?: string | null;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string | null;
  role: EmployeeRole;
  status: EmployeeStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeCreateDto {
  userId?: string | null;

  firstName: string;
  lastName: string;
  email: string;
  phone?: string | null;
  role: EmployeeRole;
}

export interface EmployeeUpdateDto {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string | null;
  role: EmployeeRole;
}