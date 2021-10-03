import { CurrentUser } from '../models/current-user.model';

export interface AuthState {
  currentUser: CurrentUser | null;
  loading: boolean;
}

export const initialState: AuthState = {
  currentUser: null,
  loading: false,
};
