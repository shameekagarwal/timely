import { createSelector } from '@ngrx/store';
import { AppState } from 'src/app/app.state';
import { UserRoles } from 'src/app/shared/constants/user-roles';
import { usersAdapter } from './users.adapter';

const rootSelector = (state: AppState) => state.Users;

const select = createSelector(
  rootSelector,
  usersAdapter.getSelectors().selectAll
);

const selectAssociates = createSelector(select, (users) =>
  users.filter((user) => user.roles.includes(UserRoles.ROLE_ASSOCIATE))
);

export const UsersSelectors = {
  select,
  selectAssociates,
};
