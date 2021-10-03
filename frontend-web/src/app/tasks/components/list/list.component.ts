import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';
import { UserRoles } from 'src/app/shared/constants/user-roles';
import { TaskDisplay } from '../../models/task-display.model';
import { Task } from '../../models/task.model';
import { TasksSelectors } from '../../state/tasks.selectors';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
})
export class ListComponent implements AfterViewInit, OnInit, OnDestroy {
  displayedColumns = ['title', 'taskStatus', 'description', 'project', 'id'];
  dataSource = new MatTableDataSource<TaskDisplay>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  subscription!: Subscription;
  isManager$!: Observable<boolean>;

  constructor(private store: Store<AppState>) {}

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngOnInit() {
    this.subscription = this.store
      .select(TasksSelectors.display)
      .subscribe((tasks) => (this.dataSource.data = tasks));

    this.isManager$ = this.store
      .select(AuthSelectors.user)
      .pipe(map((user) => user?.role === UserRoles.ROLE_MANAGER));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
