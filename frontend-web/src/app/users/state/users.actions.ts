import { createAction, props } from '@ngrx/store';
import { AppStateKeys } from 'src/app/shared/constants/app-state-keys';
import { User } from '../models/user.model';

const ActionTypes = {
  StartFetch: `[${AppStateKeys.Users}] StartFetch`,
  Set: `[${AppStateKeys.Users}] Set`,
};

const set = createAction(ActionTypes.Set, props<{ users: User[] }>());
const startFetch = createAction(ActionTypes.StartFetch);

export const UsersActions = {
  startFetch,
  set,
};
