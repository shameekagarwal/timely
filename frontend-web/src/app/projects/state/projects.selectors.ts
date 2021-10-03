import { createSelector } from '@ngrx/store';
import { AppState } from 'src/app/app.state';
import { tasksAdapter } from 'src/app/tasks/state/tasks.adapter';
import { ProjectDisplay } from '../models/project-display.model';
import { projectsAdapter } from './projects.adapter';

const rootSelector = (state: AppState) => state.Projects;

const rootTasksSelector = (state: AppState) => state.Tasks;

const tasksSelect = createSelector(
  rootTasksSelector,
  tasksAdapter.getSelectors().selectAll
);

const select = createSelector(
  rootSelector,
  projectsAdapter.getSelectors().selectAll
);

const selectDictionary = createSelector(
  rootSelector,
  projectsAdapter.getSelectors().selectEntities
);

const selectOne = (id: string) =>
  createSelector(selectDictionary, (state) => state[id]);

const loading = createSelector(rootSelector, (state) => state.loading);

const display = createSelector(
  [select, tasksSelect],
  (projects, tasks): ProjectDisplay[] => {
    return projects.map((project) => {
      const filteredTasks = tasks.filter(
        (task) => task.project.id === project.id
      );

      const TODO = filteredTasks.filter(
        (task) => task.taskStatus === 'TODO'
      ).length;
      const IN_PROGRESS = filteredTasks.filter(
        (task) => task.taskStatus === 'IN_PROGRESS'
      ).length;
      const DONE = filteredTasks.filter(
        (task) => task.taskStatus === 'DONE'
      ).length;

      return {
        id: project.id!,
        title: project.title,
        TODO,
        IN_PROGRESS,
        DONE,
      };
    });
  }
);

export const ProjectsSelectors = {
  select,
  selectDictionary,
  selectOne,
  loading,
  display,
};
