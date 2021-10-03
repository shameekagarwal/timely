import { EntityState } from '@ngrx/entity';
import { Project } from '../models/project.model';
import { projectsAdapter } from './projects.adapter';

export interface ProjectsState extends EntityState<Project> {
  loading: boolean;
}

export const initialState: ProjectsState = {
  ...projectsAdapter.getInitialState(),
  loading: false,
};
