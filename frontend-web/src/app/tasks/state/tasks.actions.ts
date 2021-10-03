import { createAction, props } from '@ngrx/store';
import { AppStateKeys } from 'src/app/shared/constants/app-state-keys';
import { Task } from '../models/task.model';

const ActionTypes = {
  StartFetch: `[${AppStateKeys.Tasks}] StartFetch`,
  Set: `[${AppStateKeys.Tasks}] Set`,

  StartCreate: `[${AppStateKeys.Tasks}] StartCreate`,
  CreateSuccess: `[${AppStateKeys.Tasks}] CreateSuccess`,
  CreateFailure: `[${AppStateKeys.Tasks}] CreateFailure`,

  StartUpdate: `[${AppStateKeys.Tasks}] StartUpdate`,
  UpdateSuccess: `[${AppStateKeys.Tasks}] UpdateSuccess`,
  UpdateFailure: `[${AppStateKeys.Tasks}] UpdateFailure`,

  StartDelete: `[${AppStateKeys.Tasks}] StartDelete`,
  DeleteSuccess: `[${AppStateKeys.Tasks}] DeleteSuccess`,
  DeleteFailure: `[${AppStateKeys.Tasks}] DeleteFailure`,
};

const set = createAction(ActionTypes.Set, props<{ tasks: Task[] }>());
const startFetch = createAction(ActionTypes.StartFetch);

const startCreate = createAction(
  ActionTypes.StartCreate,
  props<{ task: Task }>()
);
const createSuccess = createAction(
  ActionTypes.CreateSuccess,
  props<{ task: Task }>()
);
const createFailure = createAction(ActionTypes.CreateFailure);

const startUpdate = createAction(
  ActionTypes.StartUpdate,
  props<{ task: Task }>()
);
const updateSuccess = createAction(
  ActionTypes.UpdateSuccess,
  props<{ task: Task }>()
);
const updateFailure = createAction(ActionTypes.UpdateFailure);

const startDelete = createAction(
  ActionTypes.StartDelete,
  props<{ id: string }>()
);
const deleteSuccess = createAction(
  ActionTypes.DeleteSuccess,
  props<{ id: string }>()
);

export const TasksActions = {
  startFetch,
  set,

  startCreate,
  createSuccess,
  createFailure,

  startUpdate,
  updateSuccess,
  updateFailure,

  startDelete,
  deleteSuccess,
};
