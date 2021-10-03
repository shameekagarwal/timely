import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from 'src/app/app.state';
import { AuthActions } from '../../state/auth.actions';
import { AuthSelectors } from '../../state/auth.selectors';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.scss'],
})
export class SigninComponent implements OnInit {
  @ViewChild('form') form!: NgForm;
  loading$!: Observable<boolean>;

  constructor(private store: Store<AppState>) {}

  ngOnInit() {
    this.loading$ = this.store.select(AuthSelectors.loading);
  }

  submit() {
    this.store.dispatch(AuthActions.startSignin({ payload: this.form.value }));
  }
}
