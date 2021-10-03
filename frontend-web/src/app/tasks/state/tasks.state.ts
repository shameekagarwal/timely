import { EntityState } from '@ngrx/entity';
import { Task } from '../models/task.model';
import { tasksAdapter } from './tasks.adapter';

export interface TasksState extends EntityState<Task> {
  loading: boolean;
}

export const initialState: TasksState = {
  ...tasksAdapter.getInitialState(),
  loading: false,
};
