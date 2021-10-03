import { createReducer, on } from '@ngrx/store';
import { TasksActions } from './tasks.actions';
import { tasksAdapter } from './tasks.adapter';
import { initialState } from './tasks.state';

export const tasksReducer = createReducer(
  initialState,

  on(TasksActions.set, (state, action) => ({
    ...tasksAdapter.setAll(action.tasks, state),
    loading: false,
  })),

  on(TasksActions.startCreate, (state) => ({ ...state, loading: true })),
  on(TasksActions.startUpdate, (state) => ({ ...state, loading: true })),
  on(TasksActions.startDelete, (state) => ({ ...state, loading: true })),

  on(TasksActions.createFailure, (state) => ({ ...state, loading: false })),
  on(TasksActions.updateFailure, (state) => ({ ...state, loading: false })),

  on(TasksActions.createSuccess, (state, action) => ({
    ...tasksAdapter.addOne(action.task, state),
    loading: false,
  })),

  on(TasksActions.updateSuccess, (state, action) => ({
    ...tasksAdapter.updateOne(
      { changes: action.task, id: action.task.id! },
      state
    ),
    loading: false,
  })),

  on(TasksActions.deleteSuccess, (state, action) => ({
    ...tasksAdapter.removeOne(action.id, state),
    loading: false,
  }))
);
