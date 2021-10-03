import { createAction, props } from '@ngrx/store';
import { AppStateKeys } from 'src/app/shared/constants/app-state-keys';
import { Project } from '../models/project.model';

const ActionTypes = {
  StartFetch: `[${AppStateKeys.Projects}] StartFetch`,
  Set: `[${AppStateKeys.Projects}] Set`,

  StartCreate: `[${AppStateKeys.Projects}] StartCreate`,
  CreateSuccess: `[${AppStateKeys.Projects}] CreateSuccess`,
  CreateFailure: `[${AppStateKeys.Projects}] CreateFailure`,

  StartUpdate: `[${AppStateKeys.Projects}] StartUpdate`,
  UpdateSuccess: `[${AppStateKeys.Projects}] UpdateSuccess`,
  UpdateFailure: `[${AppStateKeys.Projects}] UpdateFailure`,

  StartDelete: `[${AppStateKeys.Projects}] StartDelete`,
  DeleteSuccess: `[${AppStateKeys.Projects}] DeleteSuccess`,
  DeleteFailure: `[${AppStateKeys.Projects}] DeleteFailure`,
};

const set = createAction(ActionTypes.Set, props<{ projects: Project[] }>());
const startFetch = createAction(ActionTypes.StartFetch);

const startCreate = createAction(
  ActionTypes.StartCreate,
  props<{ project: Project }>()
);
const createSuccess = createAction(
  ActionTypes.CreateSuccess,
  props<{ project: Project }>()
);
const createFailure = createAction(ActionTypes.CreateFailure);

const startUpdate = createAction(
  ActionTypes.StartUpdate,
  props<{ project: Project }>()
);
const updateSuccess = createAction(
  ActionTypes.UpdateSuccess,
  props<{ project: Project }>()
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

export const ProjectsActions = {
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
