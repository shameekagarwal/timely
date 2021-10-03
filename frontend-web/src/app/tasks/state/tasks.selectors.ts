import { createSelector } from '@ngrx/store';
import { AppState } from 'src/app/app.state';
import { projectsAdapter } from 'src/app/projects/state/projects.adapter';
import { TaskDisplay } from '../models/task-display.model';
import { tasksAdapter } from './tasks.adapter';

const rootSelector = (state: AppState) => state.Tasks;

const rootProjectsSelector = (state: AppState) => state.Projects;
const projectsSelectDictionary = createSelector(
  rootProjectsSelector,
  projectsAdapter.getSelectors().selectEntities
);

const select = createSelector(
  rootSelector,
  tasksAdapter.getSelectors().selectAll
);

const selectDictionary = createSelector(
  rootSelector,
  tasksAdapter.getSelectors().selectEntities
);

const selectOne = (id: string) =>
  createSelector(selectDictionary, (state) => state[id]);

const loading = createSelector(rootSelector, (state) => state.loading);

const display = createSelector(
  [select, projectsSelectDictionary],
  (tasks, projects): TaskDisplay[] => {
    return tasks.map((task) => ({
      id: task.id!,
      title: task.title,
      description: task.description,
      taskStatus: task.taskStatus,
      project: projects[task.project.id!]?.title!,
    }));
  }
);

export const TasksSelectors = {
  select,
  selectDictionary,
  selectOne,
  loading,
  display,
};
