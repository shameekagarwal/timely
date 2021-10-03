import { User } from '../models/user.model';
import { EntityState } from '@ngrx/entity';
import { usersAdapter } from './users.adapter';

export interface UsersState extends EntityState<User> {}

export const initialState = usersAdapter.getInitialState();
