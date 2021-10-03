import { createSelector } from '@ngrx/store';
import { AppState } from 'src/app/app.state';

const rootSelector = (state: AppState) => state.Auth;

const loading = createSelector(rootSelector, (state) => state.loading);
const isAuthenticated = createSelector(rootSelector, (state) => !!state.currentUser);
const user = createSelector(rootSelector, (state) => state.currentUser);

export const AuthSelectors = {
  loading,
  isAuthenticated,
  user,
};
