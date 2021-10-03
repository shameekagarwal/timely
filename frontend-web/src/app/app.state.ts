import { authReducer } from './auth/state/auth.reducer';
import { AuthState } from './auth/state/auth.state';
import { projectsReducer } from './projects/state/projects.reducer';
import { ProjectsState } from './projects/state/projects.state';
import { AppStateKeys } from './shared/constants/app-state-keys';
import { tasksReducer } from './tasks/state/tasks.reducer';
import { TasksState } from './tasks/state/tasks.state';
import { usersReducer as usersReducer } from './users/state/users.reducer';
import { UsersState } from './users/state/users.state';

export interface AppState {
  [AppStateKeys.Auth]: AuthState;
  [AppStateKeys.Users]: UsersState;
  [AppStateKeys.Projects]: ProjectsState;
  [AppStateKeys.Tasks]: TasksState;
}

export const appReducer = {
  [AppStateKeys.Auth]: authReducer,
  [AppStateKeys.Users]: usersReducer,
  [AppStateKeys.Projects]: projectsReducer,
  [AppStateKeys.Tasks]: tasksReducer,
};
