export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface RegisterRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface RefreshRequestDto {
  refreshToken: string;
}

export interface TokenPairDto {
  accessToken: string;
  refreshToken: string;
}

export interface UserMeDto {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  status: 'PENDING_VERIFICATION' | 'ACTIVE' | 'DISABLED';
}

export interface SessionDto {
  authenticated: boolean;
  userId?: string;
  email?: string;
}

export interface ForgotPasswordRequestDto {
  email: string;
}

export interface ResetPasswordRequestDto {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequestDto {
  oldPassword: string;
  newPassword: string;
}