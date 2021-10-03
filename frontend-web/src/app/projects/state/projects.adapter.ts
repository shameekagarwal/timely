import { createEntityAdapter } from '@ngrx/entity';
import { Project } from '../models/project.model';

export const projectsAdapter = createEntityAdapter<Project>();
