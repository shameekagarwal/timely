import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { EMPTY } from 'rxjs';
import { catchError, exhaustMap, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { FetchUsersResponse } from '../models/fetch-users-response.model';
import { User } from '../models/user.model';
import { UsersActions } from './users.actions';

@Injectable()
export class UsersEffects {
  constructor(private actions$: Actions, private http: HttpClient) {}

  fetch$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UsersActions.startFetch),

      exhaustMap(() => {
        return this.http
          .get<FetchUsersResponse[]>(`${environment.baseApiUrl}/users/`)
          .pipe(
            map((response) => {
              const users: User[] = response.map((user) => ({
                id: user.uid,
                email: user.email,
                roles: user.roles,
              }));
              return UsersActions.set({ users });
            }),
            catchError(() => EMPTY)
          );
      })
    )
  );
}
