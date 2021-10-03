export interface TaskDisplay {
  id: string;
  title: string;
  description: string;
  taskStatus: 'TODO' | 'DONE' | 'IN_PROGRESS';
  project: string;
}
