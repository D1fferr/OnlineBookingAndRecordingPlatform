import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageUserDTO, BlockUserDTO, UserQueryParams } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/user`;

  getAllUsers(params: UserQueryParams): Observable<PageUserDTO> {
    let httpParams = new HttpParams()
      .set('page', params.page ?? 0)
      .set('usersPerPage', params.usersPerPage ?? 8)
      .set('sortBy', params.sortBy ?? 'createdAt')
      .set('sortDir', params.sortDir ?? 'desc');

    if (params.search && params.search.trim()) {
      httpParams = httpParams.set('search', params.search.trim());
    }

    return this.http.get<PageUserDTO>(`${this.apiUrl}/auth/get-all-users`, { params: httpParams });
  }

  blockUser(dto: BlockUserDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/block-user`, dto);
  }

  unblockUser(userId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/unblock-user/${userId}`, {});
  }
}
