import { Component, OnDestroy, OnInit } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import firebase from 'firebase/app';
import { Subscription } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { AppState } from './app.state';
import { CurrentUser } from './auth/models/current-user.model';
import { AuthActions } from './auth/state/auth.actions';
import { ProjectsActions } from './projects/state/projects.actions';
import { TasksActions } from './tasks/state/tasks.actions';
import { UsersActions } from './users/state/users.actions';
import axios from 'axios';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'frontend-web';
  subscription!: Subscription;
  initialLoading = true;

  constructor(
    private auth: AngularFireAuth,
    private store: Store<AppState>,
    private router: Router
  ) {}

  async awakeService(url: string) {
    try {
      await axios.get(url);
    } catch (error) {}
  }

  async setState(firebaseUser: firebase.User | null) {
    // wake up sleeping heroku
    if (environment.production) {
      const awakeServices = [
        this.awakeService(environment.urlsToAwake[0]),
        this.awakeService(environment.urlsToAwake[1]),
        this.awakeService(environment.urlsToAwake[2]),
        this.awakeService(environment.urlsToAwake[3]),
        this.awakeService(environment.urlsToAwake[4]),
      ];
      await Promise.all(awakeServices);
    }

    if (!firebaseUser) {
      this.store.dispatch(AuthActions.signout());
    } else {
      const user = await CurrentUser.build(firebaseUser);
      this.store.dispatch(AuthActions.authSuccess({ user }));
      // all fetch like projects, tasks, users will be here
      this.store.dispatch(UsersActions.startFetch());
      this.store.dispatch(ProjectsActions.startFetch());
      this.store.dispatch(TasksActions.startFetch());
    }

    this.router.navigate(['/']);
    this.initialLoading = false;
  }

  ngOnInit() {
    this.subscription = this.auth.user
      .pipe(distinctUntilChanged())
      .subscribe((user) => this.setState(user));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
