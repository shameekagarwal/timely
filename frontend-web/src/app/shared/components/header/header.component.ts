import { Component, OnDestroy, OnInit } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated!: boolean;
  subscription!: Subscription;

  constructor(private store: Store<AppState>, private auth: AngularFireAuth) {}

  ngOnInit() {
    this.subscription = this.store
      .select(AuthSelectors.isAuthenticated)
      .subscribe((isAuthenticated) => (this.isAuthenticated = isAuthenticated));
  }

  signout() {
    this.auth.signOut();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
