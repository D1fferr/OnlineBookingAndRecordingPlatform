export interface UserForGetRequestDTO {
  id: string;
  email: string;
  role: string;
  avatarURL?: string;
  isBlocked: boolean;
  blockReason?: string;
}

export interface PageUserDTO {
  dtos: UserForGetRequestDTO[];
  totalPages: number;
  totalElements: number;
}

export interface BlockUserDTO {
  userId: string;
  reason: string;
}

export interface UserQueryParams {
  page?: number;
  usersPerPage?: number;
  sortBy?: string;
  sortDir?: string;
  search?: string;
}
