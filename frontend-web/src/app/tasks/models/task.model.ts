import { Project } from 'src/app/projects/models/project.model';

export interface Task {
  id?: string;
  title: string;
  description: string;
  taskStatus: 'TODO' | 'DONE' | 'IN_PROGRESS';
  project: Project;
}
