export type UserStatus =
  | 'PENDING_VERIFICATION'
  | 'ACTIVE'
  | 'DISABLED';

export interface UserReadDto {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  status: UserStatus;
}