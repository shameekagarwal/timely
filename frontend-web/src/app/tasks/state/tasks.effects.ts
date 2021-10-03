import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { EMPTY } from 'rxjs';
import { catchError, exhaustMap, map } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { NotificationService } from 'src/app/shared/services/notification.service';
import { environment } from 'src/environments/environment';
import { Task } from '../models/task.model';
import { TasksActions } from './tasks.actions';

@Injectable()
export class TasksEffects {
  constructor(
    private actions$: Actions,
    private http: HttpClient,
    private notification: NotificationService,
    private router: Router,
    private store: Store<AppState>
  ) {}

  fetch$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.startFetch),

      exhaustMap(() => {
        return this.http.get<Task[]>(`${environment.baseApiUrl}/tasks/`).pipe(
          map((tasks) => TasksActions.set({ tasks })),
          catchError(() => EMPTY)
        );
      })
    )
  );

  create$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.startCreate),

      exhaustMap((action) => {
        return this.http
          .post<Task>(`${environment.baseApiUrl}/tasks/`, action.task)
          .pipe(
            map((task) => {
              this.notification.notify('task created successfully');
              this.router.navigate(['tasks']);
              return TasksActions.createSuccess({ task });
            }),
            catchError((error) => {
              this.notification.apiError(error);
              this.store.dispatch(TasksActions.createFailure());
              return EMPTY;
            })
          );
      })
    )
  );

  update$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TasksActions.startUpdate),

      exhaustMap((action) => {
        return this.http
          .put<Task>(`${environment.baseApiUrl}/tasks/`, action.task)
          .pipe(
            map((task) => {
              this.notification.notify('task updated successfully');
              this.router.navigate(['tasks']);
              return TasksActions.updateSuccess({ task });
            }),
            catchError((error) => {
              this.notification.apiError(error);
              this.store.dispatch(TasksActions.updateFailure());
              return EMPTY;
            })
          );
      })
    )
  );

  delete$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TasksActions.startDelete),

        exhaustMap((action) => {
          return this.http
            .delete<string>(
              `${environment.baseApiUrl}/tasks/?taskId=${action.id}`
            )
            .pipe(
              catchError(() => {
                this.notification.notify('task deleted successfully');
                this.router.navigate(['tasks']);
                this.store.dispatch(
                  TasksActions.deleteSuccess({ id: action.id })
                );
                return EMPTY;
              })
            );
        })
      ),
    { dispatch: false }
  );
}
