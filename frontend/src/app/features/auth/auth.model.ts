export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  username: string;
}

export interface AuthState {
  token: string;
  username: string;
  expiresAt: number;
}
