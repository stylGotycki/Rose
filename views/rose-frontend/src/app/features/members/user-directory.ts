import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import { UserReadDto } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserDirectory {
  private readonly http = inject(HttpClient);

  searchByEmail(
    email: string,
  ): Observable<CollectionRestDto<UserReadDto>> {
    const params = new HttpParams().set(
      'email',
      email.trim(),
    );

    return this.http.get<
      CollectionRestDto<UserReadDto>
    >(
      `${API_BASE_URL}/api/users/search`,
      {
        params,
      },
    );
  }
}