import {
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { exhaustMap, take } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private store: Store<AppState>) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return this.store.select(AuthSelectors.user).pipe(
      take(1),

      exhaustMap((user) => {
        if (!user?.token) {
          return next.handle(req);
        }
        const modifiedReq = req.clone({
          headers: new HttpHeaders({ Authorization: user.token }),
        });
        return next.handle(modifiedReq);
      })
    );
  }
}
