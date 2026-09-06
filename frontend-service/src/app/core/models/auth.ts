export interface LoginDTO {
  email: string;
  password: string;
}

export interface AuthResponseDTO {
  accessToken: string;
}

export interface ResetPasswordDTO {
  code: string;
  email: string;
  newPassword: string;
}
export interface SendCodeDTO {
  email: string;
}
export interface RegistrationUserDTO {
  email: string;
  password: string;
  name: string;
  serviceType: string;
  timezone: string;
}
