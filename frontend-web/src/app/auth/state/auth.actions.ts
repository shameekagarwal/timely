import { createAction, props } from '@ngrx/store';
import { AppStateKeys } from 'src/app/shared/constants/app-state-keys';
import { SigninPayload } from '../models/signin-payload.model';
import { SignupPayload } from '../models/signup-payload.model';
import { CurrentUser } from '../models/current-user.model';

const ActionTypes = {
  StartSignin: `[${AppStateKeys.Auth}] StartSignin`,
  StartSignup: `[${AppStateKeys.Auth}] StartSignup`,
  AuthSuccess: `[${AppStateKeys.Auth}] AuthSuccess`,
  Signout: `[${AppStateKeys.Auth}] Signout`,
};

const startSignin = createAction(
  ActionTypes.StartSignin,
  props<{ payload: SigninPayload }>()
);
const startSignup = createAction(
  ActionTypes.StartSignup,
  props<{ payload: SignupPayload }>()
);
const authSuccess = createAction(
  ActionTypes.AuthSuccess,
  props<{ user: CurrentUser }>()
);
const signout = createAction(ActionTypes.Signout);

export const AuthActions = {
  startSignin,
  startSignup,
  authSuccess,
  signout,
};
