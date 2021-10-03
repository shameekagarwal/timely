import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    RouterStateSnapshot
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';
import { UserRoles } from 'src/app/shared/constants/user-roles';

@Injectable({
  providedIn: 'root',
})
export class AssociateGuard implements CanActivate {
  constructor(private store: Store<AppState>) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.store
      .select(AuthSelectors.user)
      .pipe(map((user) => user?.role === UserRoles.ROLE_ASSOCIATE));
  }
}
