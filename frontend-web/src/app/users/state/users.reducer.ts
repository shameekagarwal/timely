import { createReducer, on } from '@ngrx/store';
import { UsersActions } from './users.actions';
import { usersAdapter } from './users.adapter';
import { initialState } from './users.state';

export const usersReducer = createReducer(
  initialState,

  on(UsersActions.set, (state, action) =>
    usersAdapter.setAll(action.users, state)
  )
);
