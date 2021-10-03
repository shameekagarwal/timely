import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import axios from 'axios';
import { tap } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { NotificationService } from 'src/app/shared/services/notification.service';
import { environment } from 'src/environments/environment';
import { AuthActions } from './auth.actions';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private auth: AngularFireAuth,
    private notification: NotificationService,
    private store: Store<AppState>
  ) {}

  signin$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.startSignin),

        tap(async (action) => {
          try {
            await this.auth.signInWithEmailAndPassword(
              action.payload.email,
              action.payload.password
            );
            this.notification.notify('signed in successfully');
          } catch (error: any) {
            this.notification.notify(error.message);
            this.store.dispatch(AuthActions.signout());
          }
        })
      ),
    { dispatch: false }
  );

  signup$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.startSignup),

        tap(async (action) => {
          let token: string | null = null;
          try {
            const user = await this.auth.createUserWithEmailAndPassword(
              action.payload.email,
              action.payload.password
            );
            token = await user.user!.getIdToken();
            this.notification.notify('account created successfully');
          } catch (error: any) {
            this.notification.notify(error.message);
            this.store.dispatch(AuthActions.signout());
          }
          await axios.put(
            `${environment.baseApiUrl}/users/`,
            { role: action.payload.role },
            { headers: { Authorization: token } }
          );
        })
      ),
    { dispatch: false }
  );
}
