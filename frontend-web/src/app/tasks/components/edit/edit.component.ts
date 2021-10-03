import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { exhaustMap, take } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { Project } from 'src/app/projects/models/project.model';
import { ProjectsSelectors } from 'src/app/projects/state/projects.selectors';
import { TasksActions } from '../../state/tasks.actions';
import { TasksSelectors } from '../../state/tasks.selectors';
import { NotificationService } from '../../../shared/services/notification.service';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
})
export class EditComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  editMode!: boolean;
  editId!: string | null;
  loading$!: Observable<boolean>;
  projects: Project[] = [];
  projectsSubscription!: Subscription;

  constructor(
    private store: Store<AppState>,
    private route: ActivatedRoute,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loading$ = this.store.select(TasksSelectors.loading);

    this.projectsSubscription = this.store
      .select(ProjectsSelectors.select)
      .subscribe((projects) => (this.projects = projects));

    this.route.params.pipe(take(1)).subscribe((params) => {
      if (params.id) {
        this.editMode = true;
        this.editId = params.id;
        const task$ = this.store.select(TasksSelectors.selectOne(params.id));
        const project$ = task$.pipe(
          exhaustMap((task) =>
            this.store.select(ProjectsSelectors.selectOne(task!.project.id!))
          )
        );
        combineLatest([task$, project$])
          .pipe(take(1))
          .subscribe(([task, project]) => {
            this.form = new FormGroup({
              title: new FormControl(task!.title),
              description: new FormControl(task!.description),
              taskStatus: new FormControl(task!.taskStatus),
              project: new FormControl(project),
            });
          });
      } else {
        this.editMode = false;
        this.editId = null;
        this.form = new FormGroup({
          title: new FormControl(),
          description: new FormControl(),
          taskStatus: new FormControl(),
          project: new FormControl(),
        });
      }
    });
  }

  onSubmit() {
    const { title, description, taskStatus, project } = this.form.value;
    if (project == null) {
      this.notificationService.notify('select a project for the task');
      return;
    }
    const task = { title, description, taskStatus, project };
    if (this.editMode) {
      this.store.dispatch(
        TasksActions.startUpdate({
          task: { ...task, id: this.editId! },
        })
      );
    } else {
      this.store.dispatch(TasksActions.startCreate({ task }));
    }
  }

  onDelete() {
    this.store.dispatch(TasksActions.startDelete({ id: this.editId! }));
  }

  ngOnDestroy() {
    this.projectsSubscription.unsubscribe();
  }
}
