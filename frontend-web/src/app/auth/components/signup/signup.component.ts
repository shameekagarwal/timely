import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from 'src/app/app.state';
import { NotificationService } from 'src/app/shared/services/notification.service';
import { AuthActions } from '../../state/auth.actions';
import { AuthSelectors } from '../../state/auth.selectors';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  @ViewChild('form') form!: NgForm;
  loading$!: Observable<boolean>;

  constructor(
    private store: Store<AppState>,
    private notification: NotificationService
  ) {}

  ngOnInit() {
    this.loading$ = this.store.select(AuthSelectors.loading);
  }

  submit() {
    if (!this.form.value.role) {
      this.notification.notify('please select a role for the account');
      return;
    }
    this.store.dispatch(AuthActions.startSignup({ payload: this.form.value }));
  }
}
