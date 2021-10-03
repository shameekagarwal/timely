import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivateChild,
    RouterStateSnapshot
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';

@Injectable({
  providedIn: 'root',
})
export class AuthenticatedChildrenGuard implements CanActivateChild {
  constructor(private store: Store<AppState>) {}

  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean> {
    return this.store.select(AuthSelectors.isAuthenticated);
  }
}
