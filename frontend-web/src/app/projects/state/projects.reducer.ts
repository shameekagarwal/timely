import { createReducer, on } from '@ngrx/store';
import { ProjectsActions } from './projects.actions';
import { projectsAdapter } from './projects.adapter';
import { initialState } from './projects.state';

export const projectsReducer = createReducer(
  initialState,

  on(ProjectsActions.set, (state, action) => ({
    ...projectsAdapter.setAll(action.projects, state),
    loading: false,
  })),

  on(ProjectsActions.startCreate, (state) => ({ ...state, loading: true })),
  on(ProjectsActions.startUpdate, (state) => ({ ...state, loading: true })),
  on(ProjectsActions.startDelete, (state) => ({ ...state, loading: true })),

  on(ProjectsActions.createFailure, (state) => ({ ...state, loading: false })),
  on(ProjectsActions.updateFailure, (state) => ({ ...state, loading: false })),

  on(ProjectsActions.createSuccess, (state, action) => ({
    ...projectsAdapter.addOne(action.project, state),
    loading: false,
  })),

  on(ProjectsActions.updateSuccess, (state, action) => ({
    ...projectsAdapter.updateOne(
      { changes: action.project, id: action.project.id! },
      state
    ),
    loading: false,
  })),

  on(ProjectsActions.deleteSuccess, (state, action) => ({
    ...projectsAdapter.removeOne(action.id, state),
    loading: false,
  }))
);
