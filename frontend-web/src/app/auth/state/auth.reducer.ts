import { createReducer, on } from '@ngrx/store';
import { AuthActions } from './auth.actions';
import { initialState } from './auth.state';

export const authReducer = createReducer(
  initialState,

  on(AuthActions.startSignin, (state) => ({
    ...state,
    currentUser: null,
    loading: true,
  })),

  on(AuthActions.startSignup, (state) => ({
    ...state,
    currentUser: null,
    loading: true,
  })),

  on(AuthActions.authSuccess, (state, action) => ({
    ...state,
    currentUser: action.user,
    loading: false,
  })),

  on(AuthActions.signout, (state) => ({
    ...state,
    currentUser: null,
    loading: false,
  }))
);
