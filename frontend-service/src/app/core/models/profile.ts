export interface UserForGetRequestDTO {
  id: string;
  email: string;
  role: string;
  avatarURL?: string;
  isBlocked?: boolean;
  blockReason?: string;
}

export interface ProviderForGetRequestDTO {
  name: string;
  serviceType: string;
  timezone: string;
}

export interface ChangeCredentialsDTO {
  email?: string;
  password?: string;
  currentPassword?: string;
}

export interface ProviderChangeDataDTO {
  name?: string;
  serviceType?: string;
  timezone?: string;
}

export interface AuthResponseDTO {
  accessToken: string;
}
