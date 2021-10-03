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
import { Project } from '../models/project.model';
import { ProjectsActions } from './projects.actions';

@Injectable()
export class ProjectsEffects {
  constructor(
    private actions$: Actions,
    private http: HttpClient,
    private notification: NotificationService,
    private router: Router,
    private store: Store<AppState>
  ) {}

  fetch$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.startFetch),

      exhaustMap(() => {
        return this.http
          .get<Project[]>(`${environment.baseApiUrl}/projects/`)
          .pipe(
            map((projects) => ProjectsActions.set({ projects })),
            catchError(() => EMPTY)
          );
      })
    )
  );

  create$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.startCreate),

      exhaustMap((action) => {
        return this.http
          .post<Project>(`${environment.baseApiUrl}/projects/`, action.project)
          .pipe(
            map((project) => {
              this.notification.notify('project created successfully');
              this.router.navigate(['projects']);
              return ProjectsActions.createSuccess({ project });
            }),
            catchError((error) => {
              this.notification.apiError(error);
              this.store.dispatch(ProjectsActions.createFailure());
              return EMPTY;
            })
          );
      })
    )
  );

  update$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.startUpdate),

      exhaustMap((action) => {
        return this.http
          .put<Project>(`${environment.baseApiUrl}/projects/`, action.project)
          .pipe(
            map((project) => {
              this.notification.notify('project updated successfully');
              this.router.navigate(['projects']);
              return ProjectsActions.updateSuccess({ project });
            }),
            catchError((error) => {
              this.notification.apiError(error);
              this.store.dispatch(ProjectsActions.updateFailure());
              return EMPTY;
            })
          );
      })
    )
  );

  delete$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(ProjectsActions.startDelete),

        exhaustMap((action) => {
          return this.http
            .delete<string>(
              `${environment.baseApiUrl}/projects/?projectId=${action.id}`
            )
            .pipe(
              catchError(() => {
                this.notification.notify('project deleted successfully');
                this.router.navigate(['projects']);
                this.store.dispatch(
                  ProjectsActions.deleteSuccess({ id: action.id })
                );
                return EMPTY;
              })
            );
        })
      ),
    { dispatch: false }
  );
}
