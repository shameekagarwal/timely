import { createEntityAdapter } from '@ngrx/entity';
import { Task } from '../models/task.model';

export const tasksAdapter = createEntityAdapter<Task>();
